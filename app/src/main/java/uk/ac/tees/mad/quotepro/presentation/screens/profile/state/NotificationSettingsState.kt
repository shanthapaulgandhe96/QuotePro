package uk.ac.tees.mad.quotepro.presentation.screens.profile.state

data class NotificationSettingsState(
    val emailEnabled: Boolean = true,
    val pushEnabled: Boolean = true,
    val timing: String = "1_day_before"
)