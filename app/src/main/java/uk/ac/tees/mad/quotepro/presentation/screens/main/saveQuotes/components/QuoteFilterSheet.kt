package uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteFilterSheet(
    selectedStatus: QuoteStatus?,
    onStatusSelected: (QuoteStatus?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Filter by Status",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // All Quotes Option
            FilterStatusItem(
                status = null,
                statusText = "All Quotes",
                isSelected = selectedStatus == null,
                onClick = {
                    onStatusSelected(null)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Individual Status Options
            QuoteStatus.entries.forEach { status ->
                FilterStatusItem(
                    status = status,
                    statusText = status.name.replace("_", " "),
                    isSelected = selectedStatus == status,
                    onClick = {
                        onStatusSelected(status)
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FilterStatusItem(
    status: QuoteStatus?,
    statusText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (status != null) {
                    QuoteStatusChip(status = status, modifier = Modifier)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}