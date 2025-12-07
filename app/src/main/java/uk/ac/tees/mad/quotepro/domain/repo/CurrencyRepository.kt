package uk.ac.tees.mad.quotepro.domain.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.CurrencyRate

interface CurrencyRepository {
    suspend fun getCurrencyRates(baseCurrency: String = "USD"): Flow<Result<List<CurrencyRate>>>
    suspend fun convertAmount(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): Flow<Result<Double>>
}