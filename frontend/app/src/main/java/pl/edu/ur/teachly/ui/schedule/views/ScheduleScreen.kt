package pl.edu.ur.teachly.ui.schedule.views

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.schedule.ScheduleItemCard
import pl.edu.ur.teachly.ui.schedule.viewmodels.ScheduleViewModel

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val upcomingClasses = state.upcomingClasses
    val finishedClasses = state.finishedClasses
    val expanded = state.expanded


    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = "Terminarz",
            subtitle = if (state.isStudent) "Sprawdź swoje lekcje" else "Sprawdź swoje sesje",
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
            onBack = onBack,
            bottomPadding = 30.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

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

            upcomingClasses.isEmpty() && finishedClasses.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Brak historii zajęć.",
                    style = typography.bodyLarge,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (upcomingClasses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Brak nadchodzących zajęć.",
                                style = typography.bodyLarge,
                                color = colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    items(upcomingClasses) { upcomingClass ->
                        ScheduleItemCard(item = upcomingClass, isStudent = state.isStudent)
                    }
                }

                if (finishedClasses.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleExpanded() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Zakończone (${finishedClasses.size})",
                                style = typography.titleMedium,
                                color = colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) "Zwiń" else "Rozwiń",
                                tint = colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (expanded) {
                        items(finishedClasses) { finishedClass ->
                            ScheduleItemCard(item = finishedClass, isStudent = state.isStudent)
                        }
                    }
                }
            }
        }
    }
}
