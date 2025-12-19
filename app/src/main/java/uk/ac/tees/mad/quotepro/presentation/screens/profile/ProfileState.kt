package uk.ac.tees.mad.quotepro.presentation.screens.profile

import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference
import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings

data class ProfileState(
    // Data
    val profile: ProfileSettings = ProfileSettings(),
    val isDarkMode: Boolean = false,
    val defaultCurrency: String = "USD",
    val isBiometricEnabled: Boolean = false,
    val notificationSettings: NotificationPreference = NotificationPreference(),

    // UI Loading/Error States
    val isLoading: Boolean = false,
    val error: String? = null,

    // Dialog Visibility States
    val isEditProfileDialogVisible: Boolean = false,
    val isCurrencyDialogVisible: Boolean = false,
    val isNotificationDialogVisible: Boolean = false,
    val isLogoutDialogVisible: Boolean = false,
    val isClearCacheDialogVisible: Boolean = false,
    val isThemeDialogVisible: Boolean = false,

    // Form/Editing State (Buffers for data being edited)
    val editName: String = "",
    val editCompany: String = "",
    val editPhone: String = "",
    val editAddress: String = "",
    val isSavingProfile: Boolean = false
)