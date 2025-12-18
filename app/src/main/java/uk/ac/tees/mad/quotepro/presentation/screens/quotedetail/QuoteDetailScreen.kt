package uk.ac.tees.mad.quotepro.presentation.screens.quotedetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.presentation.navigation.EditQuoteRoute
import uk.ac.tees.mad.quotepro.presentation.screens.quotedetail.components.QuoteActionsBar
import uk.ac.tees.mad.quotepro.presentation.screens.quotedetail.components.QuoteInfoCard
import uk.ac.tees.mad.quotepro.presentation.screens.reminder.components.AddReminderDialog
import uk.ac.tees.mad.quotepro.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailScreen(
    navController: NavController,
    quoteId: String,
    viewModel: QuoteDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(quoteId) {
        viewModel.onEvent(QuoteDetailEvent.LoadQuote(quoteId))
    }

    LaunchedEffect(Unit) {
        viewModel.navAction.collect { action ->
            when (action) {
                is QuoteDetailNavAction.NavigateBack -> {
                    navController.popBackStack()
                }
                is QuoteDetailNavAction.NavigateToEdit -> {
                    navController.navigate(EditQuoteRoute(quoteId = action.quoteId))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is QuoteDetailEffect.ShowToast -> {
                    context.showToast(effect.message)
                }
                is QuoteDetailEffect.ShowError -> {
                    context.showToast(effect.error)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quote Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.onEvent(QuoteDetailEvent.LoadQuote(quoteId)) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            state.quote != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    QuoteInfoCard(
                        quote = state.quote!!,
                        modifier = Modifier.padding(16.dp)
                    )

                    QuoteActionsBar(
                        quote = state.quote!!,
                        onEditClick = {
                            viewModel.onEvent(QuoteDetailEvent.EditQuote)
                        },
                        onStatusChange = { newStatus ->
                            viewModel.onEvent(QuoteDetailEvent.UpdateStatus(newStatus))
                        },
                        onDeleteClick = {
                            viewModel.onEvent(QuoteDetailEvent.DeleteQuote)
                        },
                        onSetReminderClick = { // Wire up the event
                            viewModel.onEvent(QuoteDetailEvent.OpenReminderDialog)
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Reminder Dialog
        if (state.showReminderDialog) {
            AddReminderDialog(
                onDismiss = { viewModel.onEvent(QuoteDetailEvent.DismissReminderDialog) },
                onConfirm = { type, customDate ->
                    viewModel.onEvent(QuoteDetailEvent.SetReminder(type, customDate))
                }
            )
        }

        // Delete Confirmation Dialog
        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(QuoteDetailEvent.CancelDelete) },
                title = { Text("Delete Quote?") },
                text = { Text("Are you sure you want to delete this quote? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(QuoteDetailEvent.ConfirmDelete) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(QuoteDetailEvent.CancelDelete) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}