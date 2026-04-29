package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
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
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminLessonsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.EmptyListState
import pl.edu.ur.teachly.ui.components.other.FilterChips
import pl.edu.ur.teachly.ui.components.other.MessageSnackbars
import pl.edu.ur.teachly.ui.components.other.cards.LessonAdminCard
import pl.edu.ur.teachly.ui.components.other.dialog.LessonEditDialog

@Composable
fun AdminLessonsScreen(
    viewModel: AdminLessonsViewModel = koinViewModel(),
    initialStatusFilter: String? = null,
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf<LessonResponse?>(null) }

    LaunchedEffect(initialStatusFilter) {
        if (initialStatusFilter != null) {
            val status = LessonStatus.entries.find { it.name == initialStatusFilter }
            viewModel.onStatusFilterChange(status)
        }
    }

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
            AdminScreenHeader(title = "Lekcje") {
                AdminSearchBar(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = "Szukaj po uczestniku, przedmiocie...",
                )
                Spacer(Modifier.height(8.dp))
                FilterChips(
                    items = listOf("Wszystkie") + LessonStatus.entries.map { it.name },
                    activeItem = state.selectedStatus?.name ?: "Wszystkie",
                    onSelect = { label ->
                        viewModel.onStatusFilterChange(
                            if (label == "Wszystkie") null else LessonStatus.valueOf(label)
                        )
                    },
                )
            }
            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.filteredLessons.isEmpty() -> EmptyListState(message = "Brak lekcji")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredLessons) { lesson ->
                        LessonAdminCard(lesson = lesson, onEdit = { showEditDialog = lesson })
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

    // Edit dialog
    showEditDialog?.let { lesson ->
        LessonEditDialog(
            lesson = lesson,
            onDismiss = { showEditDialog = null },
            onSave = { request ->
                viewModel.updateLesson(lesson.id, request)
                showEditDialog = null
            }
        )
    }
}
