package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminReviewsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.EmptyListState
import pl.edu.ur.teachly.ui.components.other.MessageSnackbars
import pl.edu.ur.teachly.ui.components.other.cards.ReviewAdminCard

@Composable
fun AdminReviewsScreen(
    viewModel: AdminReviewsViewModel = koinViewModel(),
    showHeader: Boolean = true,
) {
    val state by viewModel.state.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (showHeader) {
                AdminScreenHeader(title = "Opinie korepetytorów") {
                    AdminSearchBar(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchChange(it) },
                        placeholder = "Szukaj po imieniu, nazwisku, treści...",
                    )
                }
            } else {
                Surface(color = colorScheme.surface, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AdminSearchBar(
                            value = state.searchQuery,
                            onValueChange = { viewModel.onSearchChange(it) },
                            placeholder = "Szukaj po imieniu, nazwisku, treści...",
                        )
                    }
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = state.ratingFilter == null,
                        onClick = { viewModel.onRatingFilterChange(null) },
                        label = { Text("Wszystkie") },
                    )
                }
                items((5 downTo 1).toList()) { rating ->
                    FilterChip(
                        selected = state.ratingFilter == rating,
                        onClick = { viewModel.onRatingFilterChange(rating) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text("$rating")
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        },
                    )
                }
            }

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                state.filteredReviews.isEmpty() -> EmptyListState(message = "Brak opinii")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filteredReviews) { review ->
                        ReviewAdminCard(
                            review = review,
                            onDelete = { confirmDeleteId = review.id },
                        )
                    }
                }
            }
        }

        MessageSnackbars(
            successMessage = state.successMessage,
            errorMessage = state.error,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    confirmDeleteId?.let { reviewId ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Usuń opinię") },
            text = { Text("Czy na pewno chcesz usunąć tę opinię? Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReview(reviewId)
                        confirmDeleteId = null
                    }
                ) { Text("Usuń", color = colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) { Text("Anuluj") }
            }
        )
    }
}
