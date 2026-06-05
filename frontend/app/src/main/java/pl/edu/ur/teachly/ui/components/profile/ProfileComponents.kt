package pl.edu.ur.teachly.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.cards.StatCard
import pl.edu.ur.teachly.ui.models.SubjectsByLevelGroup
import pl.edu.ur.teachly.ui.models.TutorStats
import pl.edu.ur.teachly.ui.models.displayLabel

// Shared info row
@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = typography.bodyMedium,
                color = colorScheme.onSurface
            )
        }
    }
}

// Wrapper card with automatic dividers between rows
@Composable
fun ProfileDataCard(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ProfileDataDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = colorScheme.outline.copy(alpha = 0.4f)
    )
}

private const val CollapsedLevelCount = 2

@Composable
fun SubjectChip(
    modifier: Modifier = Modifier,
    text: String,
    isOverflow: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color =
            if (isOverflow) {
                colorScheme.surfaceVariant
            } else {
                colorScheme.primaryContainer
            }
    ) {
        Text(
            text = text,
            style = typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun SubjectChipsRow(
    modifier: Modifier = Modifier,
    subjects: List<String>,
    maxVisible: Int? = null,
) {
    val visibleSubjects =
        if (maxVisible != null) {
            subjects.take(maxVisible)
        } else {
            subjects
        }
    val hiddenCount =
        if (maxVisible != null) {
            (subjects.size - maxVisible).coerceAtLeast(0)
        } else {
            0
        }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        visibleSubjects.forEach { subject ->
            SubjectChip(text = subject)
        }
        if (hiddenCount > 0) {
            SubjectChip(
                text = stringResource(R.string.subjects_more_count, hiddenCount),
                isOverflow = true
            )
        }
    }
}

@Composable
private fun SubjectLevelCard(
    title: String,
    subjectNames: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colorScheme.primaryContainer.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary
            )
            SubjectChipsRow(subjects = subjectNames)
        }
    }
}

@Composable
fun SubjectsByLevelSection(
    modifier: Modifier = Modifier,
    groups: List<SubjectsByLevelGroup>,
    otherSubjects: List<String> = emptyList(),
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val hasOtherSubjects = otherSubjects.isNotEmpty()
    val totalBlocks = groups.size + if (hasOtherSubjects) 1 else 0
    val canExpand = totalBlocks > CollapsedLevelCount

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        groups.forEachIndexed { index, group ->
            if (expanded || index < CollapsedLevelCount) {
                SubjectLevelCard(
                    title = group.level.displayLabel(),
                    subjectNames = group.subjectNames
                )
            }
        }
        if (hasOtherSubjects && (expanded || groups.size < CollapsedLevelCount)) {
            SubjectLevelCard(
                title = stringResource(R.string.teaching_level_other),
                subjectNames = otherSubjects
            )
        }
        if (canExpand) {
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text =
                        if (expanded) {
                            stringResource(R.string.subjects_see_less)
                        } else {
                            stringResource(R.string.subjects_see_more)
                        },
                    style = typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Subjects section (flat chips — fallback when levels are unavailable)
@Composable
fun SubjectsSection(subjects: List<String>) {
    SubjectChipsRow(subjects = subjects)
}

@Composable
fun AdminBadge() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = stringResource(R.string.full_permission),
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.admin_permissions),
                    style = typography.bodySmall,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TutorStatsSection(stats: TutorStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.profile_stats_title),
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    value = "${stats.completedLessons}",
                    label = stringResource(R.string.tutor_lessons_count),
                    modifier = Modifier.weight(1f),
                    compact = true
                )
                StatCard(
                    value = if (stats.avgRating > 0.0) "%.1f".format(stats.avgRating) else "–",
                    label = stringResource(R.string.avg_rating),
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    value = "${stats.reviewsCount}",
                    label = stringResource(R.string.tutor_reviews_count),
                    modifier = Modifier.weight(1f),
                    compact = true
                )
                StatCard(
                    value = stringResource(R.string.total_earnings).format(stats.totalEarnings),
                    label = stringResource(R.string.tutor_earnings),
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
        }
    }
}
