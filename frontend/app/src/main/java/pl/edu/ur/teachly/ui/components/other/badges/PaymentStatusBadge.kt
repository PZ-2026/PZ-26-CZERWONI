package pl.edu.ur.teachly.ui.components.other.badges

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.PaymentStatus

@Composable
fun PaymentStatusBadge(status: PaymentStatus) {
    val (label, color) = when (status) {
        PaymentStatus.PAID -> "Opłacone" to colorScheme.primary
        PaymentStatus.PENDING -> "Nieopłacone" to colorScheme.tertiary
        PaymentStatus.CANCELLED -> "Anulowane" to colorScheme.error
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            text = label,
            style = typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
