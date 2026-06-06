package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.profile.ProfileSectionActionIcon
import pl.edu.ur.teachly.ui.components.profile.ProfileSectionFooterAction
import pl.edu.ur.teachly.ui.components.profile.ProfileSectionTitle
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.review.views.ReviewCard

@Composable
fun TutorLessonFormatSection(tutor: Tutor) {
    if (!tutor.offersOnline && !tutor.offersInPerson) return

    DetailSection(title = stringResource(R.string.lesson_format)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tutor.offersOnline) {
                LessonFormatChip(
                    icon = Icons.Default.Wifi,
                    label = stringResource(R.string.online)
                )
            }
            if (tutor.offersInPerson) {
                LessonFormatChip(
                    icon = Icons.Default.LocationOn,
                    label = stringResource(R.string.in_person)
                )
            }
        }
    }
}

@Composable
private fun LessonFormatChip(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

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
        ProfileSectionTitle(
            title = title,
            modifier = Modifier.padding(bottom = 12.dp)
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
    onEditReview: ((ReviewResponse) -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProfileSectionTitle(title = stringResource(R.string.reviews_section_title))

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.reviews_empty),
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviews.take(3).forEach { review ->
                    ReviewCard(
                        review = review,
                        onEdit = if (onEditReview != null && review.studentId == currentStudentId) {
                            { onEditReview(review) }
                        } else {
                            null
                        }
                    )
                }
            }
            if (reviews.size > 3 && onSeeAll != null) {
                ProfileSectionFooterAction(
                    label = stringResource(R.string.reviews_see_all_btn),
                    onClick = onSeeAll,
                    icon = ProfileSectionActionIcon.Navigate
                )
            }
        }

        if (canReview && onAddReview != null) {
            PrimaryButton(
                text = stringResource(R.string.review_add_btn),
                onClick = onAddReview
            )
        }
    }
}
