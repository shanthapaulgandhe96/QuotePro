package uk.ac.tees.mad.quotepro.data.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.PreferencesDataStore
import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference
import uk.ac.tees.mad.quotepro.domain.model.UserPreferences
import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = preferencesDataStore.userPreferencesFlow

    override suspend fun updateThemeMode(isDarkMode: Boolean) {
        preferencesDataStore.updateTheme(isDarkMode)
    }

    override suspend fun updateDefaultCurrency(currencyCode: String) {
        preferencesDataStore.updateCurrency(currencyCode)
    }

    override suspend fun updateBiometricEnabled(isEnabled: Boolean) {
        preferencesDataStore.updateBiometric(isEnabled)
    }

    override suspend fun updateNotificationPreferences(preference: NotificationPreference) {
        preferencesDataStore.updateNotifications(preference)
    }

    override suspend fun clearPreferences() {
        preferencesDataStore.clear()
    }
}