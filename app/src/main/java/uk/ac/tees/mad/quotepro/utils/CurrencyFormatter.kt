package uk.ac.tees.mad.quotepro.utils

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class CurrencyFormatter @Inject constructor() {

    /**
     * Formats a double value into a currency string (e.g., "$1,234.56").
     * Uses the provided currency code to find the symbol.
     */
    fun format(amount: Double, currencyCode: String): String {
        val symbol = CurrencyUtils.getCurrencySymbol(currencyCode)
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        return "$symbol${numberFormat.format(amount)}"
    }

    /**
     * Formats a string input (like from a TextField) into a double.
     * Returns 0.0 if parsing fails.
     */
    fun parseAmount(amountStr: String): Double {
        if (amountStr.isBlank()) return 0.0
        return try {
            amountStr.replace(",", "").toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}