package uk.ac.tees.mad.quotepro.domain.repo

import uk.ac.tees.mad.quotepro.domain.model.Client

interface QuoteRepository {

    suspend fun saveQuote(client : Client, service : String, rate: String, quantity: Double): Result<Unit>
}