package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import javax.inject.Inject

class SaveQuoteUseCase @Inject constructor(
    private val repository: NewQuoteRepo
) {
    suspend operator fun invoke(quote: QuoteEntity): Result<Unit> {
        return try {
            repository.insertQuote(quote)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}