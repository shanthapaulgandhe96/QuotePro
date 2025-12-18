package uk.ac.tees.mad.quotepro.presentation.screens.quotedetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive // Added Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus

@Composable
fun QuoteActionsBar(
    quote: Quote,
    onEditClick: () -> Unit,
    onStatusChange: (QuoteStatus) -> Unit,
    onDeleteClick: () -> Unit,
    onSetReminderClick: () -> Unit, // New Callback
    modifier: Modifier = Modifier
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. Edit Button
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Quote")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Set Reminder Button (New)
            OutlinedButton(
                onClick = onSetReminderClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Remind",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Set Reminder")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Change Status Button
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showStatusMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Status: ${quote.status.name}")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Change Status",
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
                ) {
                    QuoteStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(status.name.replace("_", " "))
                                    if (status == quote.status) {
                                        Text(
                                            text = "✓",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            onClick = {
                                if (status != quote.status) {
                                    onStatusChange(status)
                                }
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Delete Button
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Quote")
            }
        }
    }
}