package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.MOCK_REVIEWS
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.profile.SubjectsSection

@Composable
fun TutorDetailBody(tutor: Tutor) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DetailSection(title = stringResource(R.string.tutor_section_bio)) {
            TutorBioSection(tutor = tutor)
        }

        DetailSection(title = stringResource(R.string.tutor_profile_subjects_title)) {
            SubjectsSection(subjects = tutor.subjects, student = false)
        }

        DetailSection(title = stringResource(R.string.tutor_section_stats)) {
            StatsGrid(tutor = tutor)
        }

        DetailSection(title = stringResource(R.string.tutor_section_reviews)) {
            ReviewList(reviews = MOCK_REVIEWS)
        }
    }
}