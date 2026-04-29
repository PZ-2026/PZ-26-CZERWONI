package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.profile.SubjectsSection
import pl.edu.ur.teachly.ui.models.Tutor

@Composable
fun TutorDetailBody(
    tutor: Tutor,
    reviews: List<ReviewResponse> = emptyList(),
    currentStudentId: Int? = null,
    canReview: Boolean = false,
    onAddReview: (() -> Unit)? = null,
    onEditReview: ((ReviewResponse) -> Unit)? = null,
    onSeeAllReviews: (() -> Unit)? = null,
) {
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

        ReviewsSection(
            reviews = reviews,
            currentStudentId = currentStudentId,
            canReview = canReview,
            onAddReview = onAddReview,
            onSeeAll = onSeeAllReviews,
            onEditReview = onEditReview,
        )
    }
}
