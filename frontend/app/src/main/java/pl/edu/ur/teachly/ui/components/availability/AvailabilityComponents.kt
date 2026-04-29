package pl.edu.ur.teachly.ui.components.availability

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse
import pl.edu.ur.teachly.ui.components.other.formatDate
import java.time.LocalDate

@Composable
fun SlotRow(
    slot: TutorAvailabilityRecurringResponse,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = colorScheme.primary,
                modifier = Modifier.size(8.dp),
            ) {}
            Text(
                text = "${slot.timeFrom.take(5)} – ${slot.timeTo.take(5)}",
                style = typography.bodyMedium,
                color = colorScheme.onSurface,
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Usuń slot",
                modifier = Modifier.size(16.dp),
                tint = colorScheme.error,
            )
        }
    }
}

@Composable
fun OverrideItem(
    override: TutorAvailabilityOverrideResponse,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.errorContainer,
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Column {
                    Text(
                        text = runCatching {
                            formatDate(LocalDate.parse(override.overrideDate))
                        }.getOrDefault(override.overrideDate),
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface,
                    )
                    Text(
                        text = if (override.timeFrom != null && override.timeTo != null)
                            "${override.timeFrom.take(5)} – ${override.timeTo.take(5)}"
                        else "Cały dzień",
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń",
                    tint = colorScheme.error,
                )
            }
        }
    }
}