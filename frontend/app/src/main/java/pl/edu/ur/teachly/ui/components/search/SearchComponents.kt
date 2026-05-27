package pl.edu.ur.teachly.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.cards.TutorCard
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.theme.AvatarColors

@Composable
fun TutorList(tutors: List<Tutor>, onTutorClick: (Tutor) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
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
                    style = typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp),
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
