package uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteUiEvent
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.ServiceItemUi
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.ServiceValidationError

@Composable
fun ServiceItemRow(
    service: ServiceItemUi,
    index: Int,
    canRemove: Boolean,
    validationError: ServiceValidationError?,
    onEvent: (NewQuoteUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Service ${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (canRemove) {
                    IconButton(
                        onClick = { onEvent(NewQuoteUiEvent.RemoveServiceItem(service.id)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove service",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Service Description
            OutlinedTextField(
                value = service.description,
                onValueChange = {
                    onEvent(NewQuoteUiEvent.ServiceDescriptionChanged(service.id, it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description *") },
                placeholder = { Text("Service description") },
                isError = validationError?.description != null,
                supportingText = {
                    validationError?.description?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quantity
                OutlinedTextField(
                    value = service.quantity,
                    onValueChange = {
                        val value = it.toDoubleOrNull() ?: 0.0
                        onEvent(NewQuoteUiEvent.ServiceQuantityChanged(service.id, value))
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Qty *") },
                    isError = validationError?.quantity != null,
                    supportingText = {
                        validationError?.quantity?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Rate
                OutlinedTextField(
                    value = service.rate,
                    onValueChange = {
                        val value = it.toDoubleOrNull() ?: 0.0
                        onEvent(NewQuoteUiEvent.ServiceRateChanged(service.id, value))
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Rate *") },
                    isError = validationError?.rate != null,
                    supportingText = {
                        validationError?.rate?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                // Amount (Read-only)
                OutlinedTextField(
                    value = String.format("%.2f", service.amount),
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                    enabled = false,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}