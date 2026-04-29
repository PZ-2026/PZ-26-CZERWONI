package pl.edu.ur.teachly.ui.tutor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.FullScreenError
import pl.edu.ur.teachly.ui.components.other.MessageSnackbars
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.tutor.TutorDetailBody
import pl.edu.ur.teachly.ui.models.Review
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.review.views.AddReviewDialog
import pl.edu.ur.teachly.ui.theme.AvatarColors
import pl.edu.ur.teachly.ui.tutor.viewmodels.TutorDetailViewModel

@Composable
fun TutorDetailScreen(
    tutorId: String,
    onBack: () -> Unit,
    onBookClick: () -> Unit,
    onSeeAllReviews: () -> Unit = {},
    viewModel: TutorDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.loadTutor(tutorId) }

    val state by viewModel.state.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<Review?>(null) }
    val successMessage = stringResource(R.string.review_submitted_success)

    LaunchedEffect(state.reviewSubmitSuccess) {
        if (state.reviewSubmitSuccess) {
            showAddDialog = false
            editingReview = null
            kotlinx.coroutines.delay(2500)
            viewModel.clearReviewSuccess()
        }
    }

    if (showAddDialog) {
        AddReviewDialog(
            isLoading = state.isSubmittingReview,
            error = state.reviewError,
            onDismiss = {
                showAddDialog = false
                viewModel.clearReviewError()
            },
            onSubmit = { rating, comment ->
                val id = tutorId.toIntOrNull() ?: return@AddReviewDialog
                viewModel.submitReview(id, rating, comment)
            },
        )
    }

    editingReview?.let { review ->
        AddReviewDialog(
            isLoading = state.isSubmittingReview,
            error = state.reviewError,
            initialRating = review.rating.toDouble(),
            initialComment = review.text,
            onDismiss = {
                editingReview = null
                viewModel.clearReviewError()
            },
            onSubmit = { rating, comment ->
                val id = tutorId.toIntOrNull() ?: return@AddReviewDialog
                viewModel.updateReview(review.id, id, rating, comment)
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.error != null -> FullScreenError(message = state.error!!)

            state.tutor != null -> {
                val t = state.tutor!!
                val avatarIndex = remember(t.id) { (t.id - 1) % AvatarColors.size }
                val profile = remember(t) {
                    StudentProfile(
                        firstName = t.name.substringBefore(" "),
                        lastName = t.name.substringAfter(" "),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ProfileHeader(
                        profile = profile,
                        avatarColor = AvatarColors[avatarIndex % AvatarColors.size],
                        onBack = onBack,
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        TutorDetailBody(
                            tutor = t,
                            reviews = state.reviews.take(3),
                            currentStudentId = state.currentStudentId,
                            onEditReview = { review -> editingReview = review },
                            onSeeAllReviews = if (state.reviews.isNotEmpty()) onSeeAllReviews else null,
                        )

                        if (state.canReview) {
                            WriteReviewCard(
                                onClick = { showAddDialog = true },
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }

                    PrimaryButton(
                        text = stringResource(R.string.tutordetail_book_cta),
                        onClick = onBookClick,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    )
                }
            }
        }

        MessageSnackbars(
            successMessage = if (state.reviewSubmitSuccess) successMessage else null,
            errorMessage = null,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun WriteReviewCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Text(
                text = stringResource(R.string.review_add_btn),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            )
        }
    }
}
