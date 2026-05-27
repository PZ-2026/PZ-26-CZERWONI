package pl.edu.ur.teachly.ui.review.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.review.viewmodels.AllReviewsViewModel

@Composable
fun AllReviewsScreen(
    tutorId: Int,
    tutorName: String,
    onBack: () -> Unit,
    viewModel: AllReviewsViewModel = koinViewModel()
) {
    LaunchedEffect(tutorId) { viewModel.loadReviews(tutorId) }

    val state by viewModel.state.collectAsState()
    var showEditDialog by rememberSaveable { mutableStateOf<ReviewResponse?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf<ReviewResponse?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorScheme.onBackground
                )
            }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = stringResource(R.string.all_reviews_title),
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                if (tutorName.isNotBlank()) {
                    Text(
                        text = tutorName,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.reviews.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.reviews_empty),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.reviews) { review ->
                    ReviewCard(
                        review = review,
                        onEdit = if (review.studentId == state.currentStudentId) {
                            { showEditDialog = review }
                        } else {
                            null
                        },
                        onDelete = if (review.studentId == state.currentStudentId) {
                            { showDeleteDialog = review }
                        } else {
                            null
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    showEditDialog?.let { review ->
        AddReviewDialog(
            isLoading = state.isSubmitting,
            error = state.error,
            initialRating = review.rating,
            initialComment = review.comment ?: "",
            onDismiss = {
                showEditDialog = null
                viewModel.clearMessage()
            },
            onSubmit = { rating, comment ->
                viewModel.updateReview(review.id, review.tutorId, rating, comment)
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { review ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Usuń opinię") },
            text = { Text("Czy na pewno chcesz usunąć swoją opinię?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReview(review.id)
                    showDeleteDialog = null
                }) { Text("Usuń", color = colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
fun ReviewCard(
    review: ReviewResponse,
    modifier: Modifier = Modifier,
    name: String = "${review.studentFirstName} ${review.studentLastName}",
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                StarRatingDisplay(rating = review.rating)
            }

            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.comment,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val createdStr = review.createdAt.take(10)
                val updatedStr = review.updatedAt.take(10)
                val wasEdited = review.updatedAt != review.createdAt
                val dateText = buildString {
                    append(runCatching { formatDate(LocalDate.parse(createdStr)) }.getOrElse { createdStr })
                    if (wasEdited) {
                        append(" (Edytowano: ")
                        append(runCatching { formatDate(LocalDate.parse(updatedStr)) }.getOrElse { updatedStr })
                        append(")")
                    }
                }
                Text(
                    text = dateText,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                if (onEdit != null || onDelete != null) {
                    Row {
                        if (onEdit != null) {
                            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edytuj",
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.primary
                                )
                            }
                        }
                        if (onDelete != null) {
                            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Usuń",
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingDisplay(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (i <= rating) {
                    colorScheme.primary
                } else {
                    colorScheme.onSurface.copy(alpha = 0.2f)
                }
            )
        }
    }
}
