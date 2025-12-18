package uk.ac.tees.mad.quotepro.presentation.screens.reminder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.quotepro.domain.model.ReminderStatus

@Composable
fun ReminderStatusChip(
    status: ReminderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        ReminderStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ReminderStatus.SENT -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Green
        ReminderStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = status.name,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview
@Composable
fun PreviewChip() {
    ReminderStatusChip(status = ReminderStatus.SENT)
}