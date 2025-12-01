package uk.ac.tees.mad.quotepro.data.remote.dto

data class CurrencyRateDto(
    val base: String = "USD",
    val date: String = "",
    val rates: Map<String, Double> = emptyMap()
)