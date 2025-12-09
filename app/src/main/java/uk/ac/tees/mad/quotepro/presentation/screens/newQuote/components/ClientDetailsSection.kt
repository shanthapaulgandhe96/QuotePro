package uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteUiEvent
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.ValidationErrors

@Composable
fun ClientDetailsSection(
    clientName: String,
    clientEmail: String,
    clientAddress: String,
    validationErrors: ValidationErrors,
    onEvent: (NewQuoteUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Client Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Client Name
            OutlinedTextField(
                value = clientName,
                onValueChange = { onEvent(NewQuoteUiEvent.ClientNameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Client Name *") },
                placeholder = { Text("Enter client full name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = validationErrors.clientName != null,
                supportingText = {
                    validationErrors.clientName?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Client Email
            OutlinedTextField(
                value = clientEmail,
                onValueChange = { onEvent(NewQuoteUiEvent.ClientEmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Client Email *") },
                placeholder = { Text("example@email.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                isError = validationErrors.clientEmail != null,
                supportingText = {
                    validationErrors.clientEmail?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Client Address
            OutlinedTextField(
                value = clientAddress,
                onValueChange = { onEvent(NewQuoteUiEvent.ClientAddressChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Client Address *") },
                placeholder = { Text("Enter full address") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                isError = validationErrors.clientAddress != null,
                supportingText = {
                    validationErrors.clientAddress?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                minLines = 2,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }
}