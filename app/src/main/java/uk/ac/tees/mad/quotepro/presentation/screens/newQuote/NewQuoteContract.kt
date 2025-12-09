package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

import uk.ac.tees.mad.quotepro.domain.model.CurrencyRate

sealed class NewQuoteUiEffect {
    data class ShowToast(val message: String) : NewQuoteUiEffect()
    object NavigateBack : NewQuoteUiEffect()
    data class ShowError(val error: String) : NewQuoteUiEffect()
}

data class NewQuoteState(
    val quoteId: String? = null,
    val clientName: String = "",
    val clientEmail: String = "",
    val clientAddress: String = "",
    val services: List<ServiceItemUi> = listOf(ServiceItemUi()),
    val selectedCurrency: String = "USD",
    val currencySymbol: String = "$",
    val exchangeRate: Double = 1.0,
    val totalAmount: Double = 0.0,
    val logoUri: String? = null,
    val signatureUri: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isOfflineMode: Boolean = false,
    val showCurrencyPicker: Boolean = false,
    val showCameraOptions: Boolean = false,
    val showPreviewDialog: Boolean = false,
    val cameraTarget: CameraTarget? = null,
    val availableCurrencies: List<CurrencyRate> = emptyList(),
    val validationErrors: ValidationErrors = ValidationErrors()
)

data class ServiceItemUi(
    val id: String = java.util.UUID.randomUUID().toString(),
    val description: String = "",
    val quantity: String = "1",
    val rate: String = "0",
    val amount: Double = 0.0
)

data class ValidationErrors(
    val clientName: String? = null,
    val clientEmail: String? = null,
    val clientAddress: String? = null,
    val services: Map<String, ServiceValidationError> = emptyMap()
)

data class ServiceValidationError(
    val description: String? = null,
    val quantity: String? = null,
    val rate: String? = null
)

enum class CameraTarget {
    LOGO, SIGNATURE
}