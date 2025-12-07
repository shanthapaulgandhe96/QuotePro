package uk.ac.tees.mad.quotepro.domain.model

import java.util.UUID

data class Quote(
    val id: String = "",
    val quoteNumber: String = "",
    val userId: String = "",
    val client: Client = Client(),
    val services: List<Service> = emptyList(),
    val currency: String = "USD",
    val currencySymbol: String = "$",
    val exchangeRate: Double = 1.0,
    val totalAmount: Double = 0.0,
    val logoUri: String? = null,
    val signatureUri: String? = null,
    val status: QuoteStatus = QuoteStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
)

data class Client(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

data class Service(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val quantity: Double = 1.0,
    val rate: Double = 0.0,
    val amount: Double = 0.0,
)

data class CurrencyRate(
    val code: String = "USD",
    val symbol: String = "$",
    val name: String = "US Dollar",
    val rate: Double = 1.0
)

enum class QuoteStatus {
    DRAFT,
    SENT,
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED
}