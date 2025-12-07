package uk.ac.tees.mad.quotepro.domain.usecase.currency

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.repo.CurrencyRepository
import javax.inject.Inject

class ConvertAmountUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Flow<Result<Double>> {
        return repository.convertAmount(amount, fromCurrency, toCurrency)
    }
}