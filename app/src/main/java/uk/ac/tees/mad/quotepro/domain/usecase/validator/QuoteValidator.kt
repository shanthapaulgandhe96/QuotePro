package uk.ac.tees.mad.quotepro.domain.usecase.validator

import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteState
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.ServiceValidationError
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.ValidationErrors
import javax.inject.Inject

class QuoteValidator @Inject constructor() {

    fun validate(state: NewQuoteState): ValidationErrors {
        val clientNameError = if (state.clientName.isBlank()) "Client name is required" else null

        val clientEmailError = when {
            state.clientEmail.isBlank() -> "Client email is required"
            !isValidEmail(state.clientEmail) -> "Invalid email format"
            else -> null
        }

        val clientAddressError = if (state.clientAddress.isBlank()) "Client address is required" else null

        val serviceErrors = mutableMapOf<String, ServiceValidationError>()

        state.services.forEach { service ->
            val descriptionError = if (service.description.isBlank()) "Description is required" else null

            val quantityError = if (service.quantity.isBlank()) {
                "Required"
            } else {
                val qty = service.quantity.toDoubleOrNull()
                if (qty == null || qty <= 0) "Invalid" else null
            }

            val rateError = if (service.rate.isBlank()) {
                "Required"
            } else {
                val rate = service.rate.toDoubleOrNull()
                if (rate == null || rate < 0) "Invalid" else null
            }

            if (descriptionError != null || quantityError != null || rateError != null) {
                serviceErrors[service.id] = ServiceValidationError(
                    description = descriptionError,
                    quantity = quantityError,
                    rate = rateError
                )
            }
        }

        return ValidationErrors(
            clientName = clientNameError,
            clientEmail = clientEmailError,
            clientAddress = clientAddressError,
            services = serviceErrors
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }
}