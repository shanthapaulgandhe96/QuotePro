package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

sealed class NewQuoteUiEvent {
    data class ClientNameChanged(val name: String) : NewQuoteUiEvent()
    data class ClientEmailChanged(val email: String) : NewQuoteUiEvent()
    data class ClientAddressChanged(val address: String) : NewQuoteUiEvent()
    data class ServiceDescriptionChanged(val id: String, val description: String) : NewQuoteUiEvent()
    data class ServiceQuantityChanged(val id: String, val quantity: Double) : NewQuoteUiEvent()
    data class ServiceRateChanged(val id: String, val rate: Double) : NewQuoteUiEvent()
    object AddServiceItem : NewQuoteUiEvent()
    data class RemoveServiceItem(val id: String) : NewQuoteUiEvent()
    data class CurrencySelected(val currency: String) : NewQuoteUiEvent()
    data class LogoCaptured(val uri: String) : NewQuoteUiEvent()
    data class SignatureCaptured(val uri: String) : NewQuoteUiEvent()
    object ShowCurrencyPicker : NewQuoteUiEvent()
    object ShowCameraOption : NewQuoteUiEvent()
    object DismissDialog : NewQuoteUiEvent()
    object SaveQuoteClicked : NewQuoteUiEvent()
}