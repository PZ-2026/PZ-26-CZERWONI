package pl.edu.ur.teachly.ui.schedule.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.FullScreenError
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.section.SectionHeader
import pl.edu.ur.teachly.ui.components.other.section.SectionItems
import pl.edu.ur.teachly.ui.schedule.viewmodels.ScheduleViewModel


@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel(),
    onBack: () -> Unit,
    onLessonClick: (lessonId: Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.load()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.schedule),
            subtitle =
                if (state.userRole == pl.edu.ur.teachly.data.model.UserRole.STUDENT || state.userRole == pl.edu.ur.teachly.data.model.UserRole.ADMIN)
                    stringResource(R.string.check_your_lessons)
                else
                    stringResource(R.string.check_your_sessions),
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
            onBack = onBack,
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.error != null -> FullScreenError(message = state.error!!)

            state.confirmedClasses.isEmpty() &&
                    state.pendingClasses.isEmpty() &&
                    state.completedClasses.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_lesson_history),
                    style = typography.bodyLarge,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 20.dp,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Confirmed
                item {
                    SectionHeader(
                        title = stringResource(R.string.confirmed),
                        count = state.confirmedClasses.size,
                        expanded = state.confirmedExpanded,
                        badgeColor = colorScheme.primary,
                        onToggle = viewModel::toggleConfirmed,
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = state.confirmedExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        SectionItems(
                            classes = state.confirmedClasses,
                            userRole = state.userRole,
                            emptyText = stringResource(R.string.no_confirmed_lessons),
                            onLessonClick = onLessonClick,
                        )
                    }
                }

                // Pending
                item {
                    SectionHeader(
                        title = stringResource(R.string.pending),
                        count = state.pendingClasses.size,
                        expanded = state.pendingExpanded,
                        badgeColor = colorScheme.tertiary,
                        onToggle = viewModel::togglePending,
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = state.pendingExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        SectionItems(
                            classes = state.pendingClasses,
                            userRole = state.userRole,
                            emptyText = stringResource(R.string.no_pending_lessons),
                            onLessonClick = onLessonClick,
                        )
                    }
                }

                // Completed
                item {
                    SectionHeader(
                        title = stringResource(R.string.completed),
                        count = state.completedClasses.size,
                        expanded = state.completedExpanded,
                        badgeColor = colorScheme.outline,
                        onToggle = viewModel::toggleCompleted,
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = state.completedExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        SectionItems(
                            classes = state.completedClasses,
                            userRole = state.userRole,
                            emptyText = stringResource(R.string.no_completed_lessons),
                            onLessonClick = onLessonClick,
                        )
                    }
                }

                // Cancelled
                item {
                    SectionHeader(
                        title = stringResource(R.string.cancelled),
                        count = state.cancelledClasses.size,
                        expanded = state.cancelledExpanded,
                        badgeColor = colorScheme.error,
                        onToggle = viewModel::toggleCancelled,
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = state.cancelledExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        SectionItems(
                            classes = state.cancelledClasses,
                            userRole = state.userRole,
                            emptyText = stringResource(R.string.no_cancelled_lessons),
                            onLessonClick = onLessonClick,
                        )
                    }
                }
            }
        }
    }
}
