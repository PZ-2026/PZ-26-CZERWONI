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
import androidx.compose.material3.MaterialTheme
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
import pl.edu.ur.teachly.ui.components.SUBJECTS
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.avatarColors
import pl.edu.ur.teachly.ui.components.tutor.TutorCard

@Composable
fun SubjectChips(activeSubject: String, onSelect: (String) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SUBJECTS.forEach { subject ->
                val isActive = activeSubject == subject
                FilterChip(
                    selected = isActive,
                    onClick = { onSelect(subject) },
                    label = { Text(subject, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isActive,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderWidth = 1.5.dp,
                        borderWidth = 1.5.dp,
                    ),
                )
            }
        }
    }
}

@Composable
fun StatsRow() {
    val tutorCount = 48

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf(
            pluralStringResource(R.plurals.home_tutors_count, tutorCount, tutorCount),
            stringResource(R.string.stat_rating, 4.9),
            stringResource(R.string.stat_response, "<2h"),
        ).forEach { label ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TutorList(tutors: List<Tutor>, onTutorClick: (Tutor) -> Unit) {
    val tutorCount = 6

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
                        tutorCount,
                        tutorCount
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            itemsIndexed(tutors) { index, tutor ->
                TutorCard(
                    tutor = tutor,
                    colors = avatarColors(index),
                    onClick = { onTutorClick(tutor) })
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
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.home_no_results_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
