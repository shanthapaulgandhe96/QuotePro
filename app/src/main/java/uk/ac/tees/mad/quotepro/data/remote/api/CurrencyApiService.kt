package uk.ac.tees.mad.quotepro.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uk.ac.tees.mad.quotepro.data.remote.dto.CurrencyRateDto

interface CurrencyApiService {

    @GET("latest/{baseCurrency}")
    suspend fun getExchangeRates(
        @Path("baseCurrency") baseCurrency: String = "USD"
    ): Response<CurrencyRateDto>

    companion object {
        const val BASE_URL = "https://api.exchangerate-api.com/v4/"
    }
}