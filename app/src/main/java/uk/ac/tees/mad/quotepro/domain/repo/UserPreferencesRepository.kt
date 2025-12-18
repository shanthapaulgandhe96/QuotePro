package uk.ac.tees.mad.quotepro.domain.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference
import uk.ac.tees.mad.quotepro.domain.model.UserPreferences

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateThemeMode(isDarkMode: Boolean)
    suspend fun updateDefaultCurrency(currencyCode: String)
    suspend fun updateBiometricEnabled(isEnabled: Boolean)
    suspend fun updateNotificationPreferences(preference: NotificationPreference)
    suspend fun clearPreferences()
}