package uk.ac.tees.mad.quotepro.domain.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity

interface NewQuoteRepo {
    suspend fun insertQuote(quote: QuoteEntity)

    suspend fun updateQuote(quote: QuoteEntity)

    suspend fun deleteQuote(quote: QuoteEntity)

    suspend fun getQuoteById(quoteId: String): QuoteEntity?

    fun getAllQuotes(userId: String): Flow<List<QuoteEntity>>
}