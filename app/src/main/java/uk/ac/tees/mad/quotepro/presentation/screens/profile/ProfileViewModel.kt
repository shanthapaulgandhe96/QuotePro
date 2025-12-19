package uk.ac.tees.mad.quotepro.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import uk.ac.tees.mad.quotepro.domain.usecase.profile.ClearCacheUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.GetUserPreferencesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.GetUserProfileUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.ToggleBiometricUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.UpdateDefaultCurrencyUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.UpdateNotificationPreferencesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.UpdateThemeModeUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.profile.UpdateUserProfileUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val updateDefaultCurrencyUseCase: UpdateDefaultCurrencyUseCase,
    private val toggleBiometricUseCase: ToggleBiometricUseCase,
    private val updateNotificationPreferencesUseCase: UpdateNotificationPreferencesUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val authRepo: FirebaseAuthRepo
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
        observePreferences()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserProfileUseCase().collect { result ->
                result.onSuccess { profile ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            // Pre-fill edit buffers
                            editName = profile.name,
                            editCompany = profile.companyName,
                            editPhone = profile.phoneNumber,
                            editAddress = profile.address
                        )
                    }
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            getUserPreferencesUseCase().collect { prefs ->
                _state.update {
                    it.copy(
                        isDarkMode = prefs.isDarkMode,
                        defaultCurrency = prefs.defaultCurrency,
                        isBiometricEnabled = prefs.isBiometricEnabled,
                        notificationSettings = prefs.notifications
                    )
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            // --- Dialog Triggers ---
            ProfileEvent.OnEditProfileClicked -> {
                // Refresh buffers before showing dialog
                val currentProfile = _state.value.profile
                _state.update {
                    it.copy(
                        isEditProfileDialogVisible = true,
                        editName = currentProfile.name,
                        editCompany = currentProfile.companyName,
                        editPhone = currentProfile.phoneNumber,
                        editAddress = currentProfile.address
                    )
                }
            }
            ProfileEvent.OnCurrencyClicked -> _state.update { it.copy(isCurrencyDialogVisible = true) }
            ProfileEvent.OnNotificationsClicked -> _state.update { it.copy(isNotificationDialogVisible = true) }
            ProfileEvent.OnLogoutClicked -> _state.update { it.copy(isLogoutDialogVisible = true) }
            ProfileEvent.OnClearCacheClicked -> _state.update { it.copy(isClearCacheDialogVisible = true) }
            ProfileEvent.OnThemeClicked -> _state.update { it.copy(isThemeDialogVisible = true) }
            ProfileEvent.DismissDialogs -> _state.update {
                it.copy(
                    isEditProfileDialogVisible = false,
                    isCurrencyDialogVisible = false,
                    isNotificationDialogVisible = false,
                    isLogoutDialogVisible = false,
                    isClearCacheDialogVisible = false,
                    isThemeDialogVisible = false
                )
            }

            // --- Profile Editing ---
            is ProfileEvent.OnProfileFieldChanged -> {
                _state.update { state ->
                    state.copy(
                        editName = event.name ?: state.editName,
                        editCompany = event.company ?: state.editCompany,
                        editPhone = event.phone ?: state.editPhone,
                        editAddress = event.address ?: state.editAddress
                    )
                }
            }
            ProfileEvent.OnSaveProfile -> saveProfile()

            // --- Preferences ---
            is ProfileEvent.OnThemeChanged -> {
                viewModelScope.launch { updateThemeModeUseCase(event.isDarkMode) }
            }
            is ProfileEvent.OnCurrencySelected -> {
                viewModelScope.launch {
                    updateDefaultCurrencyUseCase(event.currencyCode)
                    _state.update { it.copy(isCurrencyDialogVisible = false) }
                    sendEffect(ProfileEffect.ShowToast("Currency updated"))
                }
            }
            is ProfileEvent.OnBiometricToggled -> {
                viewModelScope.launch { toggleBiometricUseCase(event.enabled) }
            }
            is ProfileEvent.OnNotificationSettingsChanged -> {
                viewModelScope.launch {
                    updateNotificationPreferencesUseCase(event.settings)
                    _state.update { it.copy(isNotificationDialogVisible = false) }
                    sendEffect(ProfileEffect.ShowToast("Notification settings saved"))
                }
            }

            // --- Actions ---
            ProfileEvent.OnConfirmLogout -> logout()
            ProfileEvent.OnConfirmClearCache -> clearCache()
            ProfileEvent.RefreshProfile -> loadProfile()
        }
    }

    private fun saveProfile() {
        val currentState = _state.value

        // Basic Validation
        if (currentState.editName.isBlank()) {
            sendEffect(ProfileEffect.ShowToast("Name cannot be empty"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingProfile = true) }

            val updatedProfile = currentState.profile.copy(
                name = currentState.editName,
                companyName = currentState.editCompany,
                phoneNumber = currentState.editPhone,
                address = currentState.editAddress
            )

            updateUserProfileUseCase(updatedProfile)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isSavingProfile = false,
                            isEditProfileDialogVisible = false,
                            profile = updatedProfile
                        )
                    }
                    sendEffect(ProfileEffect.ShowToast("Profile updated successfully"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isSavingProfile = false) }
                    sendEffect(ProfileEffect.ShowToast("Error: ${error.message}"))
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLogoutDialogVisible = false) }
            authRepo.signOut().onSuccess {
                sendEffect(ProfileEffect.NavigateToSignIn)
            }.onFailure {
                sendEffect(ProfileEffect.ShowToast("Logout failed"))
            }
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            _state.update { it.copy(isClearCacheDialogVisible = false) }
            clearCacheUseCase()
            sendEffect(ProfileEffect.ShowToast("Cache cleared successfully"))
        }
    }

    private fun sendEffect(effect: ProfileEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}