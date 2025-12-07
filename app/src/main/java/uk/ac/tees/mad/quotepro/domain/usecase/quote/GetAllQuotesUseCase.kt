package uk.ac.tees.mad.quotepro.domain.usecase.quote

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import javax.inject.Inject

class GetAllQuotesUseCase @Inject constructor(
    private val repository: NewQuoteRepo
) {
    operator fun invoke(userId: String): Flow<List<QuoteEntity>> {
        return repository.getAllQuotes(userId)
    }
}