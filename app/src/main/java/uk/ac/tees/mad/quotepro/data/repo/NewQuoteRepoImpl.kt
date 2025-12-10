package uk.ac.tees.mad.quotepro.data.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.data.local.dao.QuoteDao
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import uk.ac.tees.mad.quotepro.utils.Constants
import javax.inject.Inject

class NewQuoteRepoImpl @Inject constructor(
    private val quoteDao: QuoteDao,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : NewQuoteRepo {

    override suspend fun insertQuote(quote: QuoteEntity) {
        // Always save locally first
        quoteDao.insertQuote(quote)

        // Try to sync with Firebase if online
        if (isOnline()) {
            try {
                firestore.collection(Constants.QUOTES_COLLECTION)
                    .document(quote.id)
                    .set(quote)
                    .await()

                // Mark as synced
                val syncedQuote = quote.copy(isSynced = true)
                quoteDao.updateQuote(syncedQuote)
            } catch (e: Exception) {
                // If sync fails, keep isSynced = false
                // Will be synced later by WorkManager
                android.util.Log.e("NewQuoteRepo", "Failed to sync quote: ${e.message}")
            }
        }
    }

    override suspend fun updateQuote(quote: QuoteEntity) {
        // Update locally first
        val updatedQuote = quote.copy(
            updatedAt = System.currentTimeMillis(),
            isSynced = false // Mark as unsynced since it was modified
        )
        quoteDao.updateQuote(updatedQuote)

        // Try to sync with Firebase if online
        if (isOnline()) {
            try {
                firestore.collection(Constants.QUOTES_COLLECTION)
                    .document(updatedQuote.id)
                    .set(updatedQuote)
                    .await()

                // Mark as synced
                val syncedQuote = updatedQuote.copy(isSynced = true)
                quoteDao.updateQuote(syncedQuote)
            } catch (e: Exception) {
                android.util.Log.e("NewQuoteRepo", "Failed to sync updated quote: ${e.message}")
            }
        }
    }

    override suspend fun deleteQuote(quote: QuoteEntity) {
        // Delete locally first
        quoteDao.deleteQuote(quote)

        // Try to delete from Firebase if online
        if (isOnline()) {
            try {
                firestore.collection(Constants.QUOTES_COLLECTION)
                    .document(quote.id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                android.util.Log.e("NewQuoteRepo", "Failed to delete quote from Firebase: ${e.message}")
            }
        }
    }

    override suspend fun getQuoteById(quoteId: String): QuoteEntity? {
        // Try local first
        val localQuote = quoteDao.getQuoteById(quoteId)
        if (localQuote != null) {
            return localQuote
        }

        // If not found locally and online, try Firebase
        if (isOnline()) {
            try {
                val snapshot = firestore.collection(Constants.QUOTES_COLLECTION)
                    .document(quoteId)
                    .get()
                    .await()

                val quote = snapshot.toObject(QuoteEntity::class.java)
                quote?.let {
                    // Save to local cache
                    quoteDao.insertQuote(it.copy(isSynced = true))
                }
                return quote
            } catch (e: Exception) {
                android.util.Log.e("NewQuoteRepo", "Failed to fetch quote from Firebase: ${e.message}")
            }
        }

        return null
    }

    override fun getAllQuotes(userId: String): Flow<List<QuoteEntity>> {
        // Return local data as Flow
        // WorkManager will handle syncing in background
        return quoteDao.getAllQuotes(userId)
    }

    /**
     * Sync unsynced quotes to Firebase
     */
    suspend fun syncUnsyncedQuotes(userId: String) {
        if (!isOnline()) return

        try {
            val unsyncedQuotes = quoteDao.getUnsyncedQuotes(userId)

            unsyncedQuotes.forEach { quote ->
                try {
                    firestore.collection(Constants.QUOTES_COLLECTION)
                        .document(quote.id)
                        .set(quote)
                        .await()

                    // Mark as synced
                    val syncedQuote = quote.copy(isSynced = true)
                    quoteDao.updateQuote(syncedQuote)

                    android.util.Log.d("NewQuoteRepo", "Synced quote: ${quote.id}")
                } catch (e: Exception) {
                    android.util.Log.e("NewQuoteRepo", "Failed to sync quote ${quote.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NewQuoteRepo", "Failed to get unsynced quotes: ${e.message}")
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}