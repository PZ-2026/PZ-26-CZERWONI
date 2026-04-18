package pl.edu.ur.teachly.ui.home.views

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.ScheduleItemCard
import pl.edu.ur.teachly.ui.components.other.StatCard
import pl.edu.ur.teachly.ui.home.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        AppHeader(
            title = if (state.userName.isNotBlank()) stringResource(
                R.string.hello_name,
                state.userName
            ) else stringResource(R.string.hello),
            subtitle = if (state.isStudent) stringResource(R.string.home_student_subtitle) else stringResource(
                R.string.home_tutor_subtitle
            ),
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
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
                    style = typography.bodyMedium,
                    color = colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = "${state.totalLessons}",
                            label = stringResource(R.string.completed_lessons),
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = "${state.pendingLessonsCount}",
                            label = stringResource(R.string.pending_lessons),
                        )
                    }
                }

                item {
                    Text(
                        text =
                            if (state.isStudent)
                                stringResource(R.string.upcoming_lessons)
                            else
                                stringResource(R.string.upcoming_sessions),
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                item {
                    HomeSectionHeader(
                        title = stringResource(R.string.confirmed),
                        count = state.upcomingConfirmed.size,
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
                        HomeSectionItems(
                            classes = state.upcomingConfirmed,
                            isStudent = state.isStudent,
                            emptyText = stringResource(R.string.no_confirmed_lessons),
                        )
                    }
                }

                item {
                    HomeSectionHeader(
                        title = stringResource(R.string.pending),
                        count = state.upcomingPending.size,
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
                        HomeSectionItems(
                            classes = state.upcomingPending,
                            isStudent = state.isStudent,
                            emptyText = stringResource(R.string.no_pending_lessons),
                        )
                    }
                }

                if (state.isStudent) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton(
                            text = stringResource(R.string.search_tutor),
                            onClick = onSearchClick,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(
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
private fun HomeSectionItems(
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
