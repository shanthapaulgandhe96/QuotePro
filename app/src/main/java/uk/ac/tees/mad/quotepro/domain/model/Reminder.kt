package uk.ac.tees.mad.quotepro.domain.model

import java.util.Date

data class Reminder(
    val id: String,
    val quoteId: String,
    val clientName: String, // Cached for display without joining
    val dueDate: Long?,
    val reminderDate: Long,
    val isNotified: Boolean = false,
    val isSynced: Boolean = false,
    val type: ReminderType,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ReminderType {
    BEFORE_DUE, // e.g., 3 days before
    ON_DUE,     // On the due date
    OVERDUE     // After the due date
}

enum class ReminderStatus {
    PENDING,
    SENT,
    CANCELLED
}