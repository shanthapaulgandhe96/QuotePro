package uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.presentation.navigation.NewQuoteRoute
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.components.DeleteConfirmationDialog
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.components.QuoteFilterSheet
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.components.QuoteStatusChip
import uk.ac.tees.mad.quotepro.utils.showToast
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedQuotesScreen(
    navController: NavController,
    viewModel: SavedQuotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // Removed: pullRefreshState

    LaunchedEffect(Unit) {
        viewModel.navAction.collect { action ->
            when (action) {
                is SavedQuotesNavAction.NavigateToQuoteDetail -> {
                    navController.navigate("quoteDetail/${action.quoteId}")
                }
                is SavedQuotesNavAction.NavigateToEditQuote -> {
                    navController.navigate("newQuote/${action.quoteId}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SavedQuotesEffect.ShowToast -> context.showToast(effect.message)
                is SavedQuotesEffect.ShowError -> context.showToast(effect.error)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Quotes") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(SavedQuotesEvent.ShowFilterSheet) }) {
                        Badge(
                            containerColor = if (state.selectedStatus != null) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                    // Manual refresh button remains accessible here
                    IconButton(onClick = { viewModel.onEvent(SavedQuotesEvent.RefreshQuotes) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NewQuoteRoute) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Quote",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            // Removed: .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.onEvent(SavedQuotesEvent.SearchQueryChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by client name, email...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.onEvent(SavedQuotesEvent.SearchQueryChanged(""))
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.error != null -> {
                        ErrorState(
                            message = state.error!!,
                            onRetry = { viewModel.onEvent(SavedQuotesEvent.RefreshQuotes) }
                        )
                    }
                    state.filteredQuotes.isEmpty() -> {
                        EmptyQuoteState(hasFilter = state.selectedStatus != null || searchQuery.isNotEmpty())
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.filteredQuotes) { quote ->
                                QuoteItemCard(
                                    quote = quote,
                                    onClick = {
                                        viewModel.onEvent(SavedQuotesEvent.QuoteClicked(quote.id))
                                    },
                                    onEdit = {
                                        viewModel.onEvent(SavedQuotesEvent.EditQuoteClicked(quote.id))
                                    },
                                    onDelete = {
                                        viewModel.onEvent(SavedQuotesEvent.DeleteQuoteClicked(quote))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Removed: PullRefreshIndicator
        }

        // Filter Sheet
        if (state.showFilterSheet) {
            QuoteFilterSheet(
                selectedStatus = state.selectedStatus,
                onStatusSelected = { status ->
                    viewModel.onEvent(SavedQuotesEvent.FilterByStatus(status))
                },
                onDismiss = { viewModel.onEvent(SavedQuotesEvent.HideFilterSheet) }
            )
        }

        // Delete Confirmation Dialog
        if (state.showDeleteDialog && state.quoteToDelete != null) {
            DeleteConfirmationDialog(
                quote = state.quoteToDelete!!,
                onConfirm = { viewModel.onEvent(SavedQuotesEvent.ConfirmDelete) },
                onDismiss = { viewModel.onEvent(SavedQuotesEvent.CancelDelete) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuoteItemCard(
    quote: Quote,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quote.client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = quote.quoteNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${quote.currencySymbol}${String.format("%.2f", quote.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                QuoteStatusChip(status = quote.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatDate(quote.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyQuoteState(hasFilter: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            if (hasFilter) Icons.Default.SearchOff else Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (hasFilter) "No quotes found" else "No quotes saved yet!",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hasFilter) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your search or filter",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}