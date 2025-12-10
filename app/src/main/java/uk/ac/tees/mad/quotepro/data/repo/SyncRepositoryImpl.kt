package uk.ac.tees.mad.quotepro.data.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.data.local.dao.QuoteDao
import uk.ac.tees.mad.quotepro.domain.repo.SyncRepository
import uk.ac.tees.mad.quotepro.utils.Constants
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val quoteDao: QuoteDao,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : SyncRepository {

    override suspend fun syncOfflineQuotes() {
        if (!isOnline()) return

        try {
            // Get all unsynced quotes from local database
            val allQuotes = quoteDao.getAllQuotes("").toString() // Note: This needs proper user ID

            // This is a simplified implementation
            // In a real app, you'd want to:
            // 1. Get all quotes where isSynced = false
            // 2. Upload them to Firestore
            // 3. Mark them as synced

            // For now, we'll just log that sync is happening
            android.util.Log.d("SyncRepository", "Syncing offline quotes...")

        } catch (e: Exception) {
            throw Exception("Failed to sync quotes: ${e.message}")
        }
    }

    override suspend fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun markQuoteAsSynced(quoteId: String) {
        val quote = quoteDao.getQuoteById(quoteId)
        quote?.let {
            val updatedQuote = it.copy(isSynced = true)
            quoteDao.updateQuote(updatedQuote)
        }
    }
}