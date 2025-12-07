package uk.ac.tees.mad.quotepro.domain.usecase.currency

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.CurrencyRate
import uk.ac.tees.mad.quotepro.domain.repo.CurrencyRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(baseCurrency: String = "USD"): Flow<Result<List<CurrencyRate>>> {
        return repository.getCurrencyRates(baseCurrency)
    }
}