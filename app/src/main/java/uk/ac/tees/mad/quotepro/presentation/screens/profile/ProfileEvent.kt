package uk.ac.tees.mad.quotepro.presentation.screens.profile

import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference

sealed class ProfileEvent {
    // Dialog Triggers
    object OnEditProfileClicked : ProfileEvent()
    object OnCurrencyClicked : ProfileEvent()
    object OnNotificationsClicked : ProfileEvent()
    object OnLogoutClicked : ProfileEvent()
    object OnClearCacheClicked : ProfileEvent()
    object OnThemeClicked : ProfileEvent()
    object DismissDialogs : ProfileEvent()

    // Profile Editing
    data class OnProfileFieldChanged(
        val name: String? = null,
        val company: String? = null,
        val phone: String? = null,
        val address: String? = null
    ) : ProfileEvent()
    object OnSaveProfile : ProfileEvent()

    // Preferences
    data class OnThemeChanged(val isDarkMode: Boolean) : ProfileEvent()
    data class OnCurrencySelected(val currencyCode: String) : ProfileEvent()
    data class OnBiometricToggled(val enabled: Boolean) : ProfileEvent()
    data class OnNotificationSettingsChanged(val settings: NotificationPreference) : ProfileEvent()

    // Actions
    object OnConfirmLogout : ProfileEvent()
    object OnConfirmClearCache : ProfileEvent()
    object RefreshProfile : ProfileEvent()
}