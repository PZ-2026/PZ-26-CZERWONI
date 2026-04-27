package pl.edu.ur.teachly.ui.review.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.review.viewmodels.AllReviewsViewModel

@Composable
fun AllReviewsScreen(
    tutorId: Int,
    tutorName: String,
    onBack: () -> Unit,
    viewModel: AllReviewsViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.loadReviews(tutorId) }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorScheme.onBackground,
                )
            }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = stringResource(R.string.all_reviews_title),
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                )
                if (tutorName.isNotBlank()) {
                    Text(
                        text = tutorName,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.reviews.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.reviews_empty),
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.reviews) { review ->
                    ReviewCard(review = review)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun ReviewCard(review: ReviewResponse, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${review.studentFirstName} ${review.studentLastName}",
                    style = typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                )
                StarRatingDisplay(rating = review.rating)
            }

            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.comment,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.createdAt.take(10),
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
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
                tint = if (i <= rating)
                    colorScheme.primary
                else
                    colorScheme.onSurface.copy(alpha = 0.2f),
            )
        }
    }
}
