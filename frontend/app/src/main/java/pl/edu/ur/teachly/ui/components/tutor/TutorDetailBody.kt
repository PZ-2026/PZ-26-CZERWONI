package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.profile.SubjectsSection
import pl.edu.ur.teachly.ui.models.Review
import pl.edu.ur.teachly.ui.models.Tutor

@Composable
fun TutorDetailBody(
    tutor: Tutor,
    reviews: List<Review> = emptyList(),
    currentStudentId: Int? = null,
    onEditReview: ((Review) -> Unit)? = null,
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

        if (reviews.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.tutor_section_reviews)) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    ReviewList(
                        reviews = reviews,
                        currentStudentId = currentStudentId,
                        onEditReview = onEditReview,
                    )

                    // "Zobacz więcej" sits directly below the review cards
                    if (onSeeAllReviews != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onSeeAllReviews) {
                                Text(
                                    text = stringResource(R.string.reviews_see_all_btn),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
