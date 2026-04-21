package pl.edu.ur.teachly.ui.components.other

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
import pl.edu.ur.teachly.data.model.LessonStatus

@Composable
fun LessonStatusBadge(status: LessonStatus) {
    val (label, color) = when (status) {
        LessonStatus.PENDING -> "Oczekująca" to colorScheme.tertiary
        LessonStatus.CONFIRMED -> "Potwierdzona" to colorScheme.primary
        LessonStatus.COMPLETED -> "Zakończona" to colorScheme.onSurfaceVariant
        LessonStatus.CANCELLED -> "Anulowana" to colorScheme.error
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