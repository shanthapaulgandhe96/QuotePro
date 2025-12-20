package uk.ac.tees.mad.quotepro.presentation.screens.profile.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference

@Composable
fun NotificationSettingsDialog(
    settings: NotificationPreference,
    onSettingsChanged: (NotificationPreference) -> Unit,
    onDismiss: () -> Unit
) {
    // Local state for immediate UI feedback before saving
    val pushEnabled = remember { mutableStateOf(settings.pushEnabled) }
    val emailEnabled = remember { mutableStateOf(settings.emailEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Push Switch
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push Notifications", modifier = Modifier.weight(1f))
                    Switch(
                        checked = pushEnabled.value,
                        onCheckedChange = { pushEnabled.value = it }
                    )
                }

                // Email Switch
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Email Notifications", modifier = Modifier.weight(1f))
                    Switch(
                        checked = emailEnabled.value,
                        onCheckedChange = { emailEnabled.value = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = {
                        onSettingsChanged(
                            settings.copy(
                                pushEnabled = pushEnabled.value,
                                emailEnabled = emailEnabled.value
                            )
                        )
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}