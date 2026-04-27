package pl.edu.ur.teachly.ui.components.other.badges

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.UserRole

@Composable
fun UserRoleBadge(role: UserRole?) {
    val (label, color) = when (role) {
        UserRole.ADMIN -> "ADMIN" to colorScheme.error
        UserRole.TUTOR -> "TUTOR" to colorScheme.tertiary
        else -> "STUDENT" to colorScheme.primary
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
        Text(
            label,
            style = typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}