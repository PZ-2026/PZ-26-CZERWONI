package pl.edu.ur.teachly.ui.schedule.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.ScheduledClass
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.ScheduleItemCard
import pl.edu.ur.teachly.ui.schedule.viewmodels.ScheduleViewModel

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.schedule),
            subtitle =
                if (state.isStudent)
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

            state.error != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.error!!,
                    style = typography.bodyLarge,
                    color = colorScheme.error,
                )
            }

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
                            isStudent = state.isStudent,
                            emptyText = stringResource(R.string.no_confirmed_lessons),
                        )
                    }
                }

                item {
                    SectionHeader(
                        title = stringResource(R.string.pending),
                        count = state.pendingClasses.size,
                        expanded = state.pendingExpanded,
                        badgeColor = Color(0xFFF59E0B),
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
                            isStudent = state.isStudent,
                            emptyText = stringResource(R.string.no_pending_lessons),
                        )
                    }
                }

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
                            isStudent = state.isStudent,
                            emptyText = stringResource(R.string.no_completed_lessons),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    expanded: Boolean,
    badgeColor: Color,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(badgeColor)
            )
            Text(
                text = title,
                style = typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
            )
            Text(
                text = "($count)",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) stringResource(R.string.hide) else stringResource(R.string.expand),
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SectionItems(
    classes: List<ScheduledClass>,
    isStudent: Boolean,
    emptyText: String,
) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (classes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyText,
                    style = typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.45f),
                )
            }
        } else {
            classes.forEach { item ->
                ScheduleItemCard(item = item, isStudent = isStudent)
            }
        }
    }
}
