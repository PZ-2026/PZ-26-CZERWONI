package pl.edu.ur.teachly.ui.components.other.cards

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardInfoRow(icon: @Composable () -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
        )
    }
}