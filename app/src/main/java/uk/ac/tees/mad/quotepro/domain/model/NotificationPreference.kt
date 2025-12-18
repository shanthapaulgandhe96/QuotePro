package uk.ac.tees.mad.quotepro.domain.model

data class NotificationPreference(
    val emailEnabled: Boolean = true,
    val pushEnabled: Boolean = true,
    val reminderTiming: String = "1_day_before" // Example: 1_day_before, 3_days_before, on_due_date
)