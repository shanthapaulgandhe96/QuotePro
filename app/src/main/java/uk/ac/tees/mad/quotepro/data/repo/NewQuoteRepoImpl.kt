package uk.ac.tees.mad.quotepro.data.repo

import com.google.firebase.firestore.FirebaseFirestore
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
) : NewQuoteRepo {

    override suspend fun insertQuote(quote: QuoteEntity) {
        quoteDao.insertQuote(quote = quote)
        firestore.collection(Constants.QUOTES_COLLECTION)
            .document(quote.id)
            .set(quote).await()
    }

    override suspend fun updateQuote(quote: QuoteEntity) {
        quoteDao.updateQuote(quote = quote)
        firestore.collection(Constants.QUOTES_COLLECTION)
            .document(quote.id)
            .set(quote).await()
    }

    override suspend fun deleteQuote(quote: QuoteEntity) {
        quoteDao.deleteQuote(quote = quote)
        firestore.collection(Constants.QUOTES_COLLECTION)
            .document(quote.id)
            .delete().await()
    }

    override suspend fun getQuoteById(quoteId: String): QuoteEntity? {
        return quoteDao.getQuoteById(quoteId)
    }

    override fun getAllQuotes(userId: String): Flow<List<QuoteEntity>> {
        return quoteDao.getAllQuotes(userId)
    }

}