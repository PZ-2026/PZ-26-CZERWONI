package pl.edu.ur.teachly.ui.home.views

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.StatCard
import pl.edu.ur.teachly.ui.components.schedule.ScheduleItemCard
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
            title = if (state.userName.isNotBlank()) "Witaj, ${state.userName}!" else "Witaj!",
            subtitle = if (state.isStudent) "Co dziś chcesz się nauczyć?" else "Zarządzaj swoimi lekcjami",
            background = HeaderBackground.Vertical(
                listOf(colorScheme.primary.copy(0.05f), colorScheme.primary.copy(0.8f))
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
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Stats cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = "${state.totalLessons}",
                            label = "Odbytych lekcji",
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = "${state.pendingLessons}",
                            label = "Oczekujących lekcji",
                        )
                    }
                }

                // Upcoming lessons section
                item {
                    Text(
                        text = if (state.isStudent) "Nadchodzące lekcje" else "Nadchodzące sesje",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground,
                    )
                }

                if (state.upcomingLessons.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = "Brak nadchodzących lekcji",
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(20.dp),
                            )
                        }
                    }
                } else {
                    items(state.upcomingLessons) { lesson ->
                        ScheduleItemCard(item = lesson, isStudent = state.isStudent)
                    }
                }

                // Search button (students only)
                if (state.isStudent) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        PrimaryButton(
                            text = "Szukaj korepetytora",
                            onClick = onSearchClick,
                        )
                    }
                }
            }
        }
    }
}

