package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.data.mapper.QuoteMapper
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import javax.inject.Inject

class UpdateQuoteStatusUseCase @Inject constructor(
    private val repository: NewQuoteRepo
) {
    suspend operator fun invoke(quote: Quote): Result<Unit> {
        return try {
            val quoteEntity = QuoteMapper.toEntity(quote)
            repository.updateQuote(quoteEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}