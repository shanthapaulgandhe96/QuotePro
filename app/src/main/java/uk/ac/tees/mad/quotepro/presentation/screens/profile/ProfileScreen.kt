package uk.ac.tees.mad.quotepro.presentation.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.presentation.navigation.SignInRoute
import uk.ac.tees.mad.quotepro.presentation.screens.profile.components.ProfileHeader
import uk.ac.tees.mad.quotepro.presentation.screens.profile.components.SectionDivider
import uk.ac.tees.mad.quotepro.presentation.screens.profile.components.SettingsMenuItem
import uk.ac.tees.mad.quotepro.presentation.screens.profile.components.SettingsSection
import uk.ac.tees.mad.quotepro.presentation.screens.profile.components.SettingsSwitchItem
import uk.ac.tees.mad.quotepro.presentation.screens.profile.dialog.CurrencyPickerDialog
import uk.ac.tees.mad.quotepro.presentation.screens.profile.dialog.EditProfileDialog
import uk.ac.tees.mad.quotepro.presentation.screens.profile.dialog.LogoutConfirmationDialog
import uk.ac.tees.mad.quotepro.presentation.screens.profile.dialogs.NotificationSettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Handle Side Effects (Toasts, Navigation)
    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ProfileEffect.NavigateToSignIn -> {
                    // FIX: Navigate to the actual SignInRoute defined in your NavGraph
                    navController.navigate(SignInRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is ProfileEffect.RestartApp -> {
                    // No longer needed without theme change
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Profile Header
            ProfileHeader(
                profile = state.profile,
                onEditClick = { viewModel.onEvent(ProfileEvent.OnEditProfileClicked) }
            )

            SectionDivider()

            // 2. Account Settings
            SettingsSection(title = "Account") {
                SettingsMenuItem(
                    icon = Icons.Default.Person,
                    title = "Personal Information",
                    subtitle = "Name, Company, Address",
                    onClick = { viewModel.onEvent(ProfileEvent.OnEditProfileClicked) }
                )
            }

            SectionDivider()

            // 3. Preferences
            SettingsSection(title = "Preferences") {
                // REMOVED: Theme Selection

                // Currency
                SettingsMenuItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Default Currency",
                    subtitle = state.defaultCurrency,
                    onClick = { viewModel.onEvent(ProfileEvent.OnCurrencyClicked) }
                )

                // Notifications
                SettingsMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Reminders, Email, Push",
                    onClick = { viewModel.onEvent(ProfileEvent.OnNotificationsClicked) }
                )

                // Biometric
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Login",
                    isChecked = state.isBiometricEnabled,
                    onCheckedChange = { viewModel.onEvent(ProfileEvent.OnBiometricToggled(it)) }
                )
            }

            SectionDivider()

            // 4. Data & Privacy
            SettingsSection(title = "Data & Privacy") {
                // REMOVED: Clear Cache

                SettingsMenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.onEvent(ProfileEvent.OnLogoutClicked) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Dialogs ---

    if (state.isEditProfileDialogVisible) {
        EditProfileDialog(
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    if (state.isCurrencyDialogVisible) {
        CurrencyPickerDialog(
            currentCurrency = state.defaultCurrency,
            onCurrencySelected = { viewModel.onEvent(ProfileEvent.OnCurrencySelected(it)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissDialogs) }
        )
    }

    if (state.isNotificationDialogVisible) {
        NotificationSettingsDialog(
            settings = state.notificationSettings,
            onSettingsChanged = { viewModel.onEvent(ProfileEvent.OnNotificationSettingsChanged(it)) },
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissDialogs) }
        )
    }

    if (state.isLogoutDialogVisible) {
        LogoutConfirmationDialog(
            onConfirm = { viewModel.onEvent(ProfileEvent.OnConfirmLogout) },
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissDialogs) }
        )
    }
}