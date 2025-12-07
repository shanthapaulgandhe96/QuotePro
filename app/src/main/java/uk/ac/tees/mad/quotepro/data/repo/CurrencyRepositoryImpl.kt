package uk.ac.tees.mad.quotepro.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.quotepro.data.remote.api.CurrencyApiService
import uk.ac.tees.mad.quotepro.domain.model.CurrencyRate
import uk.ac.tees.mad.quotepro.domain.repo.CurrencyRepository
import uk.ac.tees.mad.quotepro.utils.CurrencyUtils
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService,
) : CurrencyRepository {

    private var cachedRates: Map<String, Double>? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION = 3600000L // 1 hour

    override suspend fun getCurrencyRates(baseCurrency: String): Flow<Result<List<CurrencyRate>>> = flow {
        try {
            // Check if we have fresh cached data
            if (cachedRates != null &&
                System.currentTimeMillis() - lastFetchTime < CACHE_DURATION) {
                val currencies = buildCurrencyList(cachedRates!!)
                emit(Result.success(currencies))
                return@flow
            }

            // Fetch from API
            val response = currencyApiService.getExchangeRates(baseCurrency)

            if (response.isSuccessful && response.body() != null) {
                val rateDto = response.body()!!
                cachedRates = rateDto.rates
                lastFetchTime = System.currentTimeMillis()

                val currencies = buildCurrencyList(rateDto.rates)
                emit(Result.success(currencies))
            } else {
                emit(Result.failure(Exception("Failed to fetch currency rates: ${response.message()}")))
            }

        } catch (e: Exception) {
            // If API fails, try to use cached data
            if (cachedRates != null) {
                val currencies = buildCurrencyList(cachedRates!!)
                emit(Result.success(currencies))
            } else {
                emit(Result.failure(Exception(e.message ?: "Failed to fetch currency rates")))
            }
        }
    }

    override suspend fun convertAmount(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Flow<Result<Double>> = flow {
        try {
            // If same currency, no conversion needed
            if (fromCurrency == toCurrency) {
                emit(Result.success(amount))
                return@flow
            }

            // Get rates for from currency
            val ratesFlow = getCurrencyRates(fromCurrency)

            ratesFlow.collect { result ->
                result.onSuccess { currencies ->
                    val targetCurrency = currencies.find { it.code == toCurrency }

                    if (targetCurrency != null) {
                        val convertedAmount = amount * targetCurrency.rate
                        emit(Result.success(convertedAmount))
                    } else {
                        emit(Result.failure(Exception("Target currency not found")))
                    }
                }
                result.onFailure {
                    emit(Result.failure(Exception(it.message ?: "Conversion failed")))
                }
            }

        } catch (e: Exception) {
            emit(Result.failure(Exception(e.message ?: "Failed to convert amount")))
        }
    }

    private fun buildCurrencyList(rates: Map<String, Double>): List<CurrencyRate> {
        return CurrencyUtils.supportedCurrencies.mapNotNull { currencyInfo ->
            rates[currencyInfo.code]?.let { rate ->
                CurrencyRate(
                    code = currencyInfo.code,
                    symbol = currencyInfo.symbol,
                    name = currencyInfo.name,
                    rate = rate
                )
            }
        }
    }
}