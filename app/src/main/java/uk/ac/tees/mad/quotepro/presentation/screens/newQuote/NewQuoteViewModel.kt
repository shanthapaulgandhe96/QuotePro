package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.data.mapper.QuoteMapper
import uk.ac.tees.mad.quotepro.domain.model.Client
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import uk.ac.tees.mad.quotepro.domain.model.Service
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import uk.ac.tees.mad.quotepro.domain.usecase.currency.GetExchangeRatesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetQuoteByIdUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SaveQuoteUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.storage.UploadImageUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewQuoteViewModel @Inject constructor(
    private val saveQuoteUseCase: SaveQuoteUseCase,
    private val getQuoteByIdUseCase: GetQuoteByIdUseCase,
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val authRepo: FirebaseAuthRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(NewQuoteState())
    val state: StateFlow<NewQuoteState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewQuoteUiEffect>()
    val effect = _effect.asSharedFlow()

    private val quoteId: String? = savedStateHandle.get<String>("quoteId")

    init {
        loadCurrencies()
        quoteId?.let { loadQuote(it) }
    }

    fun onEvent(event: NewQuoteUiEvent) {
        when (event) {
            is NewQuoteUiEvent.ClientNameChanged -> updateClientName(event.name)
            is NewQuoteUiEvent.ClientEmailChanged -> updateClientEmail(event.email)
            is NewQuoteUiEvent.ClientAddressChanged -> updateClientAddress(event.address)
            is NewQuoteUiEvent.ServiceDescriptionChanged -> updateServiceDescription(event.id, event.description)
            is NewQuoteUiEvent.ServiceQuantityChanged -> updateServiceQuantity(event.id, event.quantity)
            is NewQuoteUiEvent.ServiceRateChanged -> updateServiceRate(event.id, event.rate)
            is NewQuoteUiEvent.AddServiceItem -> addServiceItem()
            is NewQuoteUiEvent.RemoveServiceItem -> removeServiceItem(event.id)
            is NewQuoteUiEvent.CurrencySelected -> updateCurrency(event.currency)
            is NewQuoteUiEvent.LogoCaptured -> handleLogoCaptured(event.uri)
            is NewQuoteUiEvent.SignatureCaptured -> handleSignatureCaptured(event.uri)
            is NewQuoteUiEvent.ShowCurrencyPicker -> showCurrencyPicker()
            is NewQuoteUiEvent.ShowCameraOption -> showCameraOptions()
            is NewQuoteUiEvent.DismissDialog -> dismissDialogs()
            is NewQuoteUiEvent.SaveQuoteClicked -> validateAndSaveQuote()
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                getExchangeRatesUseCase().collect { result ->
                    result.onSuccess { currencies ->
                        _state.update {
                            it.copy(
                                availableCurrencies = currencies,
                                isLoading = false
                            )
                        }
                    }
                    result.onFailure { error ->
                        _state.update { it.copy(isLoading = false, isOfflineMode = true) }
                        _effect.emit(NewQuoteUiEffect.ShowToast("Using offline mode"))
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isOfflineMode = true) }
                _effect.emit(NewQuoteUiEffect.ShowToast("Using offline mode"))
            }
        }
    }

    private fun loadQuote(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getQuoteByIdUseCase(id)
                .onSuccess { quoteEntity ->
                    if (quoteEntity != null) {
                        val quote = QuoteMapper.toDomain(quoteEntity)
                        populateStateFromQuote(quote)
                    } else {
                        _effect.emit(NewQuoteUiEffect.ShowError("Quote not found"))
                        _effect.emit(NewQuoteUiEffect.NavigateBack)
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(NewQuoteUiEffect.ShowError(error.message ?: "Failed to load quote"))
                }
        }
    }

    private fun populateStateFromQuote(quote: Quote) {
        val services = quote.services.map { service ->
            ServiceItemUi(
                id = service.id,
                description = service.description,
                quantity = service.quantity.toString(),
                rate = service.rate.toString(),
                amount = service.amount
            )
        }

        _state.update {
            it.copy(
                quoteId = quote.id,
                clientName = quote.client.name,
                clientEmail = quote.client.email,
                clientAddress = quote.client.address,
                services = services,
                selectedCurrency = quote.currency,
                currencySymbol = quote.currencySymbol,
                exchangeRate = quote.exchangeRate,
                logoUri = quote.logoUri,
                signatureUri = quote.signatureUri,
                totalAmount = quote.totalAmount,
                isLoading = false
            )
        }
    }

    private fun updateClientName(name: String) {
        _state.update { it.copy(clientName = name) }
        clearValidationError("clientName")
    }

    private fun updateClientEmail(email: String) {
        _state.update { it.copy(clientEmail = email) }
        clearValidationError("clientEmail")
    }

    private fun updateClientAddress(address: String) {
        _state.update { it.copy(clientAddress = address) }
        clearValidationError("clientAddress")
    }

    private fun updateServiceDescription(id: String, description: String) {
        _state.update {
            it.copy(
                services = it.services.map { service ->
                    if (service.id == id) {
                        service.copy(description = description)
                    } else service
                }
            )
        }
        clearServiceValidationError(id, "description")
    }

    private fun updateServiceQuantity(id: String, quantity: Double) {
        _state.update {
            val updatedServices = it.services.map { service ->
                if (service.id == id) {
                    val rate = service.rate.toDoubleOrNull() ?: 0.0
                    val amount = quantity * rate
                    service.copy(
                        quantity = quantity.toString(),
                        amount = amount
                    )
                } else service
            }
            it.copy(
                services = updatedServices,
                totalAmount = calculateTotal(updatedServices)
            )
        }
        clearServiceValidationError(id, "quantity")
    }

    private fun updateServiceRate(id: String, rate: Double) {
        _state.update {
            val updatedServices = it.services.map { service ->
                if (service.id == id) {
                    val quantity = service.quantity.toDoubleOrNull() ?: 0.0
                    val amount = quantity * rate
                    service.copy(
                        rate = rate.toString(),
                        amount = amount
                    )
                } else service
            }
            it.copy(
                services = updatedServices,
                totalAmount = calculateTotal(updatedServices)
            )
        }
        clearServiceValidationError(id, "rate")
    }

    private fun calculateTotal(services: List<ServiceItemUi>): Double {
        return services.sumOf { it.amount }
    }

    private fun addServiceItem() {
        _state.update {
            it.copy(services = it.services + ServiceItemUi())
        }
    }

    private fun removeServiceItem(id: String) {
        _state.update {
            val updatedServices = it.services.filter { service -> service.id != id }
            it.copy(
                services = updatedServices,
                totalAmount = calculateTotal(updatedServices)
            )
        }
    }

    private fun updateCurrency(currency: String) {
        viewModelScope.launch {
            val currencyRate = _state.value.availableCurrencies.find { it.code == currency }
            if (currencyRate != null) {
                _state.update {
                    it.copy(
                        selectedCurrency = currencyRate.code,
                        currencySymbol = currencyRate.symbol,
                        exchangeRate = currencyRate.rate
                    )
                }
            }
        }
    }

    private fun handleLogoCaptured(uri: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userId = authRepo.getCurrentUser()?.uid ?: run {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(NewQuoteUiEffect.ShowError("User not authenticated"))
                return@launch
            }

            uploadImageUseCase.uploadLogo(userId, Uri.parse(uri))
                .onSuccess { cloudinaryUrl ->
                    _state.update {
                        it.copy(
                            logoUri = cloudinaryUrl,
                            isLoading = false
                        )
                    }
                    _effect.emit(NewQuoteUiEffect.ShowToast("Logo uploaded successfully"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(NewQuoteUiEffect.ShowError(error.message ?: "Failed to upload logo"))
                }
        }
    }

    private fun handleSignatureCaptured(uri: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userId = authRepo.getCurrentUser()?.uid ?: run {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(NewQuoteUiEffect.ShowError("User not authenticated"))
                return@launch
            }

            uploadImageUseCase.uploadSignature(userId, Uri.parse(uri))
                .onSuccess { cloudinaryUrl ->
                    _state.update {
                        it.copy(
                            signatureUri = cloudinaryUrl,
                            isLoading = false
                        )
                    }
                    _effect.emit(NewQuoteUiEffect.ShowToast("Signature uploaded successfully"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(NewQuoteUiEffect.ShowError(error.message ?: "Failed to upload signature"))
                }
        }
    }

    private fun showCurrencyPicker() {
        _state.update { it.copy(showCurrencyPicker = true) }
    }

    private fun showCameraOptions() {
        _state.update { it.copy(showCameraOptions = true) }
    }

    private fun dismissDialogs() {
        _state.update {
            it.copy(
                showCurrencyPicker = false,
                showCameraOptions = false,
                showPreviewDialog = false
            )
        }
    }

    private fun validateAndSaveQuote() {
        val validationErrors = validateInputs()

        if (validationErrors.hasErrors()) {
            _state.update { it.copy(validationErrors = validationErrors) }
            viewModelScope.launch {
                _effect.emit(NewQuoteUiEffect.ShowError("Please fix all errors before saving"))
            }
            return
        }

        _state.update { it.copy(showPreviewDialog = true) }
    }

    fun confirmSaveQuote() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, showPreviewDialog = false) }

            val userId = authRepo.getCurrentUser()?.uid ?: run {
                _state.update { it.copy(isSaving = false) }
                _effect.emit(NewQuoteUiEffect.ShowError("User not authenticated"))
                return@launch
            }

            val currentState = _state.value
            val quote = buildQuoteFromState(currentState, userId)
            val quoteEntity = QuoteMapper.toEntity(quote)

            saveQuoteUseCase(quoteEntity)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(NewQuoteUiEffect.ShowToast("Quote saved successfully"))
                    _effect.emit(NewQuoteUiEffect.NavigateBack)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(NewQuoteUiEffect.ShowError(error.message ?: "Failed to save quote"))
                }
        }
    }

    private fun buildQuoteFromState(state: NewQuoteState, userId: String): Quote {
        val services = state.services.map { serviceUi ->
            Service(
                id = serviceUi.id,
                description = serviceUi.description,
                quantity = serviceUi.quantity.toDoubleOrNull() ?: 0.0,
                rate = serviceUi.rate.toDoubleOrNull() ?: 0.0,
                amount = serviceUi.amount
            )
        }

        val client = Client(
            id = UUID.randomUUID().toString(),
            name = state.clientName,
            email = state.clientEmail,
            address = state.clientAddress
        )

        return Quote(
            id = state.quoteId ?: UUID.randomUUID().toString(),
            quoteNumber = generateQuoteNumber(),
            userId = userId,
            client = client,
            services = services,
            currency = state.selectedCurrency,
            currencySymbol = state.currencySymbol,
            exchangeRate = state.exchangeRate,
            totalAmount = state.totalAmount,
            logoUri = state.logoUri,
            signatureUri = state.signatureUri,
            status = QuoteStatus.DRAFT,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun generateQuoteNumber(): String {
        val timestamp = System.currentTimeMillis()
        return "QP-${timestamp.toString().takeLast(8)}"
    }

    private fun validateInputs(): ValidationErrors {
        val errors = mutableMapOf<String, ServiceValidationError>()
        var clientNameError: String? = null
        var clientEmailError: String? = null
        var clientAddressError: String? = null

        if (_state.value.clientName.isBlank()) {
            clientNameError = "Client name is required"
        }

        if (_state.value.clientEmail.isBlank()) {
            clientEmailError = "Client email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_state.value.clientEmail).matches()) {
            clientEmailError = "Invalid email format"
        }

        if (_state.value.clientAddress.isBlank()) {
            clientAddressError = "Client address is required"
        }

        _state.value.services.forEach { service ->
            var descError: String? = null
            var qtyError: String? = null
            var rateError: String? = null

            if (service.description.isBlank()) {
                descError = "Description required"
            }

            val qty = service.quantity.toDoubleOrNull()
            if (qty == null || qty <= 0) {
                qtyError = "Invalid quantity"
            }

            val rate = service.rate.toDoubleOrNull()
            if (rate == null || rate < 0) {
                rateError = "Invalid rate"
            }

            if (descError != null || qtyError != null || rateError != null) {
                errors[service.id] = ServiceValidationError(descError, qtyError, rateError)
            }
        }

        return ValidationErrors(
            clientName = clientNameError,
            clientEmail = clientEmailError,
            clientAddress = clientAddressError,
            services = errors
        )
    }

    private fun clearValidationError(field: String) {
        _state.update {
            it.copy(
                validationErrors = when (field) {
                    "clientName" -> it.validationErrors.copy(clientName = null)
                    "clientEmail" -> it.validationErrors.copy(clientEmail = null)
                    "clientAddress" -> it.validationErrors.copy(clientAddress = null)
                    else -> it.validationErrors
                }
            )
        }
    }

    private fun clearServiceValidationError(serviceId: String, field: String) {
        _state.update {
            val currentErrors = it.validationErrors.services[serviceId]
            if (currentErrors != null) {
                val updatedError = when (field) {
                    "description" -> currentErrors.copy(description = null)
                    "quantity" -> currentErrors.copy(quantity = null)
                    "rate" -> currentErrors.copy(rate = null)
                    else -> currentErrors
                }

                val updatedServices = if (updatedError.hasNoErrors()) {
                    it.validationErrors.services - serviceId
                } else {
                    it.validationErrors.services + (serviceId to updatedError)
                }

                it.copy(
                    validationErrors = it.validationErrors.copy(services = updatedServices)
                )
            } else {
                it
            }
        }
    }
}

private fun ValidationErrors.hasErrors(): Boolean {
    return clientName != null || clientEmail != null || clientAddress != null || services.isNotEmpty()
}

private fun ServiceValidationError.hasNoErrors(): Boolean {
    return description == null && quantity == null && rate == null
}