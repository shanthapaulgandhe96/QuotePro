package uk.ac.tees.mad.quotepro.presentation.screens.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.presentation.navigation.QuoteDetailRoute
import uk.ac.tees.mad.quotepro.presentation.screens.reminder.components.AddReminderDialog
import uk.ac.tees.mad.quotepro.presentation.screens.reminder.components.QuoteSelectionDialog
import uk.ac.tees.mad.quotepro.presentation.screens.reminder.components.ReminderStatusChip
import uk.ac.tees.mad.quotepro.utils.showToast
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReminderUiEffect.ShowToast -> context.showToast(effect.message)
                is ReminderUiEffect.NavigateToQuote -> {
                    navController.navigate(QuoteDetailRoute(effect.quoteId))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ReminderUiEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ReminderUiEvent.AddReminderClicked) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(ReminderUiEvent.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search client...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredReminders.isEmpty()) {
                EmptyReminderState()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.filteredReminders) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onClick = { viewModel.onEvent(ReminderUiEvent.ReminderClicked(reminder)) },
                            onDelete = { viewModel.onEvent(ReminderUiEvent.DeleteReminderClicked(reminder)) }
                        )
                    }
                }
            }
        }

        if (state.showQuoteSelectionDialog) {
            QuoteSelectionDialog(
                quotes = state.availableQuotes,
                onDismiss = { viewModel.onEvent(ReminderUiEvent.DismissDialogs) },
                onQuoteSelected = { quote ->
                    viewModel.onEvent(ReminderUiEvent.QuoteSelectedForReminder(quote))
                }
            )
        }

        if (state.showAddDialog) {
            AddReminderDialog(
                onDismiss = { viewModel.onEvent(ReminderUiEvent.DismissDialogs) },
                onConfirm = { type, date ->
                    viewModel.onEvent(
                        ReminderUiEvent.CreateReminder(
                            quoteId = state.quoteIdForReminder,
                            clientName = state.clientNameForReminder,
                            dueDate = state.quoteDueDateForReminder,
                            type = type,
                            customDate = date
                        )
                    )
                }
            )
        }

        if (state.showDeleteDialog && state.selectedReminder != null) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(ReminderUiEvent.DismissDialogs) },
                title = { Text("Delete Reminder?") },
                text = { Text("This will cancel the scheduled notification.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(ReminderUiEvent.ConfirmDelete) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(ReminderUiEvent.DismissDialogs) }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(reminder.clientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Remind on: ${sdf.format(Date(reminder.reminderDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
                ReminderStatusChip(status = reminder.status)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun EmptyReminderState() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.NotificationsOff, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(16.dp))
        Text("No reminders found", style = MaterialTheme.typography.bodyLarge)
    }
}