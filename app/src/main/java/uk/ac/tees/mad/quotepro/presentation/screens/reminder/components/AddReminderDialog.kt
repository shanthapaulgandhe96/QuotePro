package uk.ac.tees.mad.quotepro.presentation.screens.reminder.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.domain.model.ReminderType
import java.util.*

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (ReminderType, Long?) -> Unit
) {
    var selectedType by remember { mutableStateOf(ReminderType.BEFORE_DUE) }
    // Ideally, add a DatePicker here for custom dates, simplified for now

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Reminder") },
        text = {
            Column {
                Text("When should we remind you?")
                Spacer(Modifier.height(8.dp))

                ReminderType.entries.forEach { type ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedType == type),
                            onClick = { selectedType = type }
                        )
                        Text(
                            text = when(type) {
                                ReminderType.BEFORE_DUE -> "3 Days Before Due"
                                ReminderType.ON_DUE -> "On Due Date"
                                ReminderType.OVERDUE -> "1 Day Overdue"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedType, null) }) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}