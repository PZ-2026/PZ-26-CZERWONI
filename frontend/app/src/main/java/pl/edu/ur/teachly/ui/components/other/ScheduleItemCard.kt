package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.ScheduledClass
import java.time.LocalTime

@Composable
fun ScheduleItemCard(
    item: ScheduledClass,
    isStudent: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Subject and status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.subject,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                LessonStatusBadge(status = item.status)
            }

            Spacer(Modifier.height(8.dp))

            // Person row — tutor for student, student for tutor
            val personLabel =
                if (isStudent) stringResource(R.string.tutor) else stringResource(R.string.student)
            val personName = if (isStudent) item.tutorName else item.studentName
            if (personName.isNotBlank()) {
                InfoRow(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = colorScheme.primary
                        )
                    },
                    text = "$personLabel: $personName",
                )
                Spacer(Modifier.height(6.dp))
            }

            // Date
            InfoRow(
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = formatDate(item.day),
            )

            Spacer(Modifier.height(6.dp))

            // Time and duration
            InfoRow(
                icon = {
                    Icon(
                        Icons.Default.Schedule,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = run {
                    val end = LocalTime.parse(item.time).plusMinutes(item.durationMinutes.toLong())
                    "${item.time} - ${end.toString().take(5)} (${item.durationMinutes} min)"
                }
            )
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val containerColor = when (status) {
        stringResource(R.string.confirmed) -> colorScheme.primaryContainer
        stringResource(R.string.pending) -> colorScheme.inversePrimary
        stringResource(R.string.completed) -> colorScheme.surfaceVariant
        else -> colorScheme.surface
    }
    Surface(shape = MaterialTheme.shapes.small, color = containerColor) {
        Text(
            text = status,
            style = typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = colorScheme.onSurface,
        )
    }
}

@Composable
private fun InfoRow(icon: @Composable () -> Unit, text: String) {
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
