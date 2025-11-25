package uk.ac.tees.mad.quotepro.data.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo

class NewQuoteRepoImpl : NewQuoteRepo {
    override suspend fun insertQuote(quote: QuoteEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuote(quote: QuoteEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuote(quote: QuoteEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getQuoteById(quoteId: String): QuoteEntity? {
        TODO("Not yet implemented")
    }

    override fun getAllQuotes(userId: String): Flow<List<QuoteEntity>> {
        TODO("Not yet implemented")
    }

}