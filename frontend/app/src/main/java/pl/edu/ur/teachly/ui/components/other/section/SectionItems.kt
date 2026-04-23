package pl.edu.ur.teachly.ui.components.other.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.cards.ScheduleItemCard
import pl.edu.ur.teachly.ui.models.ScheduledClass

@Composable
fun SectionItems(
    classes: List<ScheduledClass>,
    userRole: UserRole,
    emptyText: String,
    onLessonClick: (lessonId: Int) -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (classes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyText,
                    style = typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.45f),
                )
            }
        } else {
            classes.forEach { item ->
                ScheduleItemCard(
                    item = item,
                    userRole = userRole,
                    onClick = { onLessonClick(item.id.toIntOrNull() ?: return@ScheduleItemCard) },
                )
            }
        }
    }
}
