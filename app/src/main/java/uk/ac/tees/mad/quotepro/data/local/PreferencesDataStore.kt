package uk.ac.tees.mad.quotepro.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference
import uk.ac.tees.mad.quotepro.domain.model.UserPreferences
import uk.ac.tees.mad.quotepro.utils.PreferencesKeys
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        val isDark = preferences[PreferencesKeys.THEME_MODE] ?: false
        val currency = preferences[PreferencesKeys.DEFAULT_CURRENCY] ?: "USD"
        val biometric = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false

        val emailNotif = preferences[PreferencesKeys.EMAIL_NOTIFICATIONS] ?: true
        val pushNotif = preferences[PreferencesKeys.PUSH_NOTIFICATIONS] ?: true

        UserPreferences(
            isDarkMode = isDark,
            defaultCurrency = currency,
            isBiometricEnabled = biometric,
            notifications = NotificationPreference(emailEnabled = emailNotif, pushEnabled = pushNotif)
        )
    }

    suspend fun updateTheme(isDark: Boolean) {
        dataStore.edit { it[PreferencesKeys.THEME_MODE] = isDark }
    }

    suspend fun updateCurrency(currency: String) {
        dataStore.edit { it[PreferencesKeys.DEFAULT_CURRENCY] = currency }
    }

    suspend fun updateBiometric(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun updateNotifications(notif: NotificationPreference) {
        dataStore.edit {
            it[PreferencesKeys.EMAIL_NOTIFICATIONS] = notif.emailEnabled
            it[PreferencesKeys.PUSH_NOTIFICATIONS] = notif.pushEnabled
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}