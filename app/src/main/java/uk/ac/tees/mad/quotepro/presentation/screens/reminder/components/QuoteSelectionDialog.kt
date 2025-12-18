package uk.ac.tees.mad.quotepro.presentation.screens.reminder.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.domain.model.Quote
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QuoteSelectionDialog(
    quotes: List<Quote>,
    onDismiss: () -> Unit,
    onQuoteSelected: (Quote) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a Quote") },
        text = {
            if (quotes.isEmpty()) {
                Text("No quotes found. Create a quote first.")
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp), // Limit height
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quotes) { quote ->
                        QuoteSelectionItem(quote = quote, onClick = { onQuoteSelected(quote) })
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun QuoteSelectionItem(quote: Quote, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quote.client.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "#${quote.quoteNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = sdf.format(Date(quote.createdAt)),
            style = MaterialTheme.typography.bodySmall
        )
    }
}