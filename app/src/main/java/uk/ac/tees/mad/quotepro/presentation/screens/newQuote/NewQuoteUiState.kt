package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

import java.util.UUID

data class NewQuoteUiState(
    val quoteId: String? = null,
    val clientName: String = "",
    val clientEmail: String = "",
    val clientAddress: String = "",
    val services: List<ServiceItem> = emptyList(),
    val selectedCurrency: String = "USD",
    val currencySymbol: String = "$",
    val exchangeRate: Double = 1.0,
    val totalAmount: Double = 0.0,
    val selectedTemplate: String = "default",
    val logoUri: String? = null,
    val signatureUri: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isOfflineMode: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val showCurrencyPicker: Boolean = false,
    val showTemplateSelector: Boolean = false,
    val showCameraOption: Boolean = false,
    val availableCurrencies: List<String> = emptyList(),
)

data class ServiceItem(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val quantity: Double = 1.0,
    val rate: Double = 0.0,
    val amount: Double = 0.0
)