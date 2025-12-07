package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import javax.inject.Inject

class GetQuoteByIdUseCase @Inject constructor(
    private val repository: NewQuoteRepo
) {
    suspend operator fun invoke(quoteId: String): Result<QuoteEntity?> {
        return try {
            val quote = repository.getQuoteById(quoteId)
            Result.success(quote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}