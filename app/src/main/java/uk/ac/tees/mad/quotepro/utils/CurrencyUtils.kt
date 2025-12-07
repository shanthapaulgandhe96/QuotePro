package uk.ac.tees.mad.quotepro.utils

data class CurrencyInfo(
    val code: String,
    val symbol: String,
    val name: String
)

object CurrencyUtils {
    val supportedCurrencies = listOf(
        CurrencyInfo("USD", "$", "US Dollar"),
        CurrencyInfo("EUR", "€", "Euro"),
        CurrencyInfo("GBP", "£", "British Pound"),
        CurrencyInfo("JPY", "¥", "Japanese Yen"),
        CurrencyInfo("INR", "₹", "Indian Rupee"),
        CurrencyInfo("AUD", "A$", "Australian Dollar"),
        CurrencyInfo("CAD", "C$", "Canadian Dollar"),
        CurrencyInfo("CHF", "Fr", "Swiss Franc"),
        CurrencyInfo("CNY", "¥", "Chinese Yuan"),
        CurrencyInfo("SEK", "kr", "Swedish Krona"),
        CurrencyInfo("NZD", "NZ$", "New Zealand Dollar"),
        CurrencyInfo("KRW", "₩", "South Korean Won"),
        CurrencyInfo("SGD", "S$", "Singapore Dollar"),
        CurrencyInfo("NOK", "kr", "Norwegian Krone"),
        CurrencyInfo("MXN", "$", "Mexican Peso"),
        CurrencyInfo("BRL", "R$", "Brazilian Real"),
        CurrencyInfo("ZAR", "R", "South African Rand"),
        CurrencyInfo("HKD", "HK$", "Hong Kong Dollar"),
        CurrencyInfo("DKK", "kr", "Danish Krone"),
        CurrencyInfo("PLN", "zł", "Polish Zloty")
    )

    fun getCurrencySymbol(code: String): String {
        return supportedCurrencies.find { it.code == code }?.symbol ?: "$"
    }

    fun getCurrencyName(code: String): String {
        return supportedCurrencies.find { it.code == code }?.name ?: "Unknown"
    }
}