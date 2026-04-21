package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.profile.SubjectsSection
import pl.edu.ur.teachly.ui.models.Review
import pl.edu.ur.teachly.ui.models.Tutor

@Composable
fun TutorDetailBody(tutor: Tutor, reviews: List<Review> = emptyList()) {
    Column(
        modifier = Modifier.padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DetailSection(title = stringResource(R.string.tutor_section_bio)) {
            TutorBioSection(tutor = tutor)
        }

        if (tutor.subjects.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.tutor_profile_subjects_title)) {
                SubjectsSection(subjects = tutor.subjects, student = false)
            }
        }

        if (reviews.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.tutor_section_reviews)) {
                ReviewList(reviews = reviews)
            }
        }
    }
}