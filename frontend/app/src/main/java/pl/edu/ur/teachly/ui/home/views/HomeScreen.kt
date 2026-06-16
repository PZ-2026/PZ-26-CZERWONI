package pl.edu.ur.teachly.ui.home.views

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.FullScreenError
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.LoadingBox
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.cards.StatCard
import pl.edu.ur.teachly.ui.components.other.section.SectionHeader
import pl.edu.ur.teachly.ui.components.other.section.SectionItems
import pl.edu.ur.teachly.ui.home.viewmodels.HomeViewModel
import pl.edu.ur.teachly.ui.models.ScheduledClass
import pl.edu.ur.teachly.ui.theme.headerGradientColors
import pl.edu.ur.teachly.ui.review.views.PendingReviewFormDialog
import pl.edu.ur.teachly.ui.review.views.PendingReviewsSummaryDialog

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
    onLessonClick: (lessonId: Int) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.review_submitted_success)
    val confirmedBadgeColor = colorScheme.primary

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.load()
        }
    }

    // Show snackbar after pending review submitted
    LaunchedEffect(state.pendingReviewSubmitted) {
        if (state.pendingReviewSubmitted) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.clearPendingReviewSubmitted()
        }
    }

    // Auto-open the form when there is exactly 1 pending review
    LaunchedEffect(state.pendingReviews) {
        if (state.pendingReviews.size == 1 && state.selectedPendingReview == null) {
            viewModel.selectPendingReview(state.pendingReviews[0])
        }
    }

    // Summary dialog — multiple unreviewed tutors
    if (state.pendingReviews.size > 1 && state.selectedPendingReview == null) {
        PendingReviewsSummaryDialog(
            reviews = state.pendingReviews,
            onSelect = viewModel::selectPendingReview,
            onDismiss = viewModel::dismissAllPendingReviews
        )
    }

    // Full review form — single selected tutor
    state.selectedPendingReview?.let { pending ->
        PendingReviewFormDialog(
            pending = pending,
            isLoading = state.isSubmittingPendingReview,
            error = state.pendingReviewError,
            onDismiss = {
                if (state.pendingReviews.size <= 1) {
                    viewModel.dismissAllPendingReviews()
                } else {
                    viewModel.dismissSelectedPendingReview()
                }
            },
            onSubmit = { rating, comment -> viewModel.submitPendingReview(rating, comment) }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            AppHeader(
                title = if (state.userName.isNotBlank()) {
                    stringResource(
                        R.string.hello_name,
                        state.userName
                    )
                } else {
                    stringResource(R.string.hello)
                },
                subtitle =
                if (state.userRole == UserRole.STUDENT) {
                    stringResource(R.string.home_student_subtitle)
                } else {
                    stringResource(R.string.home_tutor_subtitle)
                },
                background = HeaderBackground.Diagonal(headerGradientColors()),
                showLogo = true
            )

            when {
                state.isLoading -> LoadingBox()

                state.error != null -> FullScreenError(message = state.error!!)

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                value = "${state.totalLessons}",
                                label = stringResource(R.string.completed_lessons)
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                value = "${state.pendingLessonsCount}",
                                label = stringResource(R.string.pending_lessons)
                            )
                        }
                    }

                    if (state.userRole == UserRole.STUDENT) {
                        item {
                            PrimaryButton(
                                text = stringResource(R.string.search_tutor),
                                onClick = onSearchClick,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    item {
                        Text(
                            text =
                            if (state.userRole == UserRole.STUDENT) {
                                stringResource(R.string.upcoming_lessons)
                            } else {
                                stringResource(R.string.upcoming_sessions)
                            },
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    expandableLessonSection(
                        titleRes = R.string.confirmed,
                        classes = state.upcomingConfirmed,
                        userRole = state.userRole,
                        expanded = state.confirmedExpanded,
                        badgeColor = confirmedBadgeColor,
                        emptyTextRes = R.string.no_confirmed_lessons,
                        onToggle = viewModel::toggleConfirmed,
                        onLessonClick = onLessonClick
                    )

                    expandableLessonSection(
                        titleRes = R.string.pending,
                        classes = state.upcomingPending,
                        userRole = state.userRole,
                        expanded = state.pendingExpanded,
                        badgeColor = Color(0xFFF59E0B),
                        emptyTextRes = R.string.no_pending_lessons,
                        onToggle = viewModel::togglePending,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

/**
 * Rozwijalna sekcja listy lekcji na ekranie głównym: nagłówek [SectionHeader] z licznikiem oraz
 * animowana lista [SectionItems]. Wydzielona, aby uniknąć powtarzania identycznej struktury dla
 * lekcji potwierdzonych i oczekujących.
 */
private fun LazyListScope.expandableLessonSection(
    @StringRes titleRes: Int,
    classes: List<ScheduledClass>,
    userRole: UserRole,
    expanded: Boolean,
    badgeColor: Color,
    @StringRes emptyTextRes: Int,
    onToggle: () -> Unit,
    onLessonClick: (lessonId: Int) -> Unit
) {
    item {
        SectionHeader(
            title = stringResource(titleRes),
            count = classes.size,
            expanded = expanded,
            badgeColor = badgeColor,
            onToggle = onToggle
        )
    }
    item {
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            SectionItems(
                classes = classes,
                userRole = userRole,
                emptyText = stringResource(emptyTextRes),
                onLessonClick = onLessonClick
            )
        }
    }
}
