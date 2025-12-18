package uk.ac.tees.mad.quotepro.domain.model

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val defaultCurrency: String = "USD",
    val isBiometricEnabled: Boolean = false,
    val notifications: NotificationPreference = NotificationPreference()
)