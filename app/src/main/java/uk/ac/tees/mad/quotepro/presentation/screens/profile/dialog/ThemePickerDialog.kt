package uk.ac.tees.mad.quotepro.presentation.screens.profile.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ThemePickerDialog(
    isDarkMode: Boolean,
    onThemeSelected: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Theme", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onThemeSelected(false) }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = !isDarkMode, onClick = { onThemeSelected(false) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Light Mode")
                }

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onThemeSelected(true) }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isDarkMode, onClick = { onThemeSelected(true) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dark Mode")
                }
            }
        }
    }
}