package pl.edu.ur.teachly.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.tutor.TutorCard
import pl.edu.ur.teachly.ui.theme.AvatarColors

@Composable
fun SubjectChips(
    subjects: List<String>,
    activeSubject: String,
    onSelect: (String) -> Unit,
) {
    Surface(color = colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            subjects.forEach { subject ->
                val isActive = activeSubject == subject
                FilterChip(
                    selected = isActive,
                    onClick = { onSelect(subject) },
                    label = { Text(subject, style = typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary,
                        containerColor = colorScheme.surfaceVariant,
                        labelColor = colorScheme.onSurfaceVariant,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isActive,
                        selectedBorderColor = colorScheme.primary,
                        borderColor = colorScheme.outline,
                        selectedBorderWidth = 1.5.dp,
                        borderWidth = 1.5.dp,
                    ),
                )
            }
        }
    }
}

@Composable
fun StatsRow(tutorCount: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf(
            pluralStringResource(R.plurals.home_tutors_count, tutorCount, tutorCount),
            stringResource(R.string.stat_rating, 4.9),
        ).forEach { label ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.surface,
                border = BorderStroke(1.dp, colorScheme.outline),
            ) {
                Text(
                    text = label,
                    style = typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TutorList(tutors: List<Tutor>, onTutorClick: (Tutor) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        if (tutors.isEmpty()) {
            item { EmptyState() }
        } else {
            item {
                Text(
                    text = pluralStringResource(
                        R.plurals.home_tutors_count,
                        tutors.size,
                        tutors.size,
                    ),
                    style = typography.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            itemsIndexed(tutors) { index, tutor ->
                TutorCard(
                    tutor = tutor,
                    colors = AvatarColors[index % AvatarColors.size],
                    onClick = { onTutorClick(tutor) },
                )
                if (index < tutors.lastIndex) Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.home_no_results),
            style = typography.titleSmall,
            color = colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.home_no_results_hint),
            style = typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}
