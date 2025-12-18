package uk.ac.tees.mad.quotepro.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME_MODE = booleanPreferencesKey("theme_mode") // true = dark, false = light/system
    val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
    val EMAIL_NOTIFICATIONS = booleanPreferencesKey("email_notifications")
    val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    val USER_NAME = stringPreferencesKey("user_name") // Basic cache
    val COMPANY_NAME = stringPreferencesKey("company_name")
}