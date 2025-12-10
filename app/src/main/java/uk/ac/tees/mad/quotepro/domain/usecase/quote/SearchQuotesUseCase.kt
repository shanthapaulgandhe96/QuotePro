package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.domain.model.Quote
import javax.inject.Inject

class SearchQuotesUseCase @Inject constructor() {

    operator fun invoke(quotes: List<Quote>, query: String): List<Quote> {
        if (query.isBlank()) return quotes

        val searchQuery = query.lowercase().trim()

        return quotes.filter { quote ->
            quote.client.name.lowercase().contains(searchQuery) ||
                    quote.client.email.lowercase().contains(searchQuery) ||
                    quote.quoteNumber.lowercase().contains(searchQuery) ||
                    quote.services.any { service ->
                        service.description.lowercase().contains(searchQuery)
                    }
        }
    }
}