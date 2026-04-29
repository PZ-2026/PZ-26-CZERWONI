package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.review.views.ReviewCard

@Composable
fun TutorBioSection(tutor: Tutor) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Text(
                text = tutor.bio,
                style = typography.bodyMedium,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        content()
    }
}

@Composable
fun ReviewsSection(
    reviews: List<ReviewResponse>,
    currentStudentId: Int? = null,
    canReview: Boolean = false,
    onAddReview: (() -> Unit)? = null,
    onSeeAll: (() -> Unit)? = null,
    onEditReview: ((ReviewResponse) -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.reviews_section_title),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
            )
            if (reviews.size > 3 && onSeeAll != null) {
                TextButton(onClick = onSeeAll) {
                    Text(
                        text = stringResource(R.string.reviews_see_all_btn),
                        style = typography.labelMedium,
                        color = colorScheme.primary,
                    )
                }
            }
        }

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.reviews_empty),
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviews.take(3).forEach { review ->
                    ReviewCard(
                        review = review,
                        onEdit = if (onEditReview != null && review.studentId == currentStudentId) {
                            { onEditReview(review) }
                        } else null,
                    )
                }
            }
            if (reviews.size > 3 && onSeeAll != null) {
                OutlinedButton(
                    onClick = onSeeAll,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.reviews_see_all_btn))
                }
            }
        }

        if (canReview && onAddReview != null) {
            PrimaryButton(
                text = stringResource(R.string.review_add_btn),
                onClick = onAddReview,
            )
        }
    }
}
