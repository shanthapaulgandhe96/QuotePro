package uk.ac.tees.mad.quotepro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val quoteNumber: String,
    val userId: String,
    val clientJson: String, //// JSON string of client
    val servicesJson: String, // JSON string of services list
    val currency: String,
    val currencySymbol: String,
    val exchangeRate: Double,
    val totalAmount: Double,
    val logoUri: String?,
    val signatureUri: String?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val dueDate: Long?,
    val isSynced: Boolean = false,
)
