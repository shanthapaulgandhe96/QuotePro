package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import javax.inject.Inject

class FilterQuotesByStatusUseCase @Inject constructor() {

    operator fun invoke(quotes: List<Quote>, status: QuoteStatus?): List<Quote> {
        return if (status == null) {
            quotes
        } else {
            quotes.filter { it.status == status }
        }
    }
}