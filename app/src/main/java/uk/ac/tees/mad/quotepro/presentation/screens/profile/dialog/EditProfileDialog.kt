package uk.ac.tees.mad.quotepro.presentation.screens.profile.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.tees.mad.quotepro.presentation.screens.profile.ProfileEvent
import uk.ac.tees.mad.quotepro.presentation.screens.profile.ProfileState

@Composable
fun EditProfileDialog(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    Dialog(onDismissRequest = { onEvent(ProfileEvent.DismissDialogs) }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = state.editName,
                    onValueChange = { onEvent(ProfileEvent.OnProfileFieldChanged(name = it)) },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.editCompany,
                    onValueChange = { onEvent(ProfileEvent.OnProfileFieldChanged(company = it)) },
                    label = { Text("Company Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.editPhone,
                    onValueChange = { onEvent(ProfileEvent.OnProfileFieldChanged(phone = it)) },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.editAddress,
                    onValueChange = { onEvent(ProfileEvent.OnProfileFieldChanged(address = it)) },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onEvent(ProfileEvent.DismissDialogs) }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(ProfileEvent.OnSaveProfile) },
                        enabled = !state.isSavingProfile
                    ) {
                        if (state.isSavingProfile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}