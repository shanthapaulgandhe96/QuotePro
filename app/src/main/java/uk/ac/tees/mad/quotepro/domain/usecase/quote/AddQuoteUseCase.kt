package uk.ac.tees.mad.quotepro.domain.usecase.quote

import androidx.lifecycle.ViewModel
import uk.ac.tees.mad.quotepro.domain.model.Client
import uk.ac.tees.mad.quotepro.domain.repo.QuoteRepository
import javax.inject.Inject

class AddQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    suspend operator fun invoke(client: Client,
                                service: String,
                                rate: String,
                                quantity: Double
    ): Result<Unit>{
        return quoteRepository.saveQuote(client,service,rate,quantity)
    }

}