package uk.ac.tees.mad.quotepro.presentation.screens.saveQuotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus

@Composable
fun QuoteStatusChip(
    status: QuoteStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getStatusColors(status)

    Text(
        text = status.name.replace("_", " "),
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun getStatusColors(status: QuoteStatus): Pair<Color, Color> {
    return when (status) {
        QuoteStatus.DRAFT -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        QuoteStatus.SENT -> Pair(
            Color(0xFF2196F3).copy(alpha = 0.2f),
            Color(0xFF1976D2)
        )
        QuoteStatus.PENDING -> Pair(
            Color(0xFFFFC107).copy(alpha = 0.2f),
            Color(0xFFF57C00)
        )
        QuoteStatus.PAID -> Pair(
            Color(0xFF4CAF50).copy(alpha = 0.2f),
            Color(0xFF2E7D32)
        )
        QuoteStatus.OVERDUE -> Pair(
            Color(0xFFF44336).copy(alpha = 0.2f),
            Color(0xFFC62828)
        )
        QuoteStatus.CANCELLED -> Pair(
            Color(0xFF9E9E9E).copy(alpha = 0.2f),
            Color(0xFF616161)
        )
    }
}