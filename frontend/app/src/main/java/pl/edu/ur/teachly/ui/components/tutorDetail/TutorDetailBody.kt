package pl.edu.ur.teachly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.home.Tutor

@Composable
fun TutorDetailBody(tutor: Tutor) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        TagRow(tags = tutor.tags)
        Spacer(Modifier.height(24.dp))

        DetailSection(title = stringResource(R.string.section_about)) {
            Text(text = tutor.bio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        DetailSection(title = stringResource(R.string.section_stats)) {
            StatsGrid(tutor = tutor)
        }

        DetailSection(title = stringResource(R.string.section_reviews)) {
            ReviewList(reviews = MOCK_REVIEWS)
        }
    }
}
