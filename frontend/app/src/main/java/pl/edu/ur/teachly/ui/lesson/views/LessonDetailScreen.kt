package pl.edu.ur.teachly.ui.lesson.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.lesson.ActionsSection
import pl.edu.ur.teachly.ui.components.lesson.InfoCard
import pl.edu.ur.teachly.ui.components.lesson.NotesSection
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.lesson.viewmodels.LessonDetailViewModel

@Composable
fun LessonDetailScreen(
    lessonId: Int,
    onBack: () -> Unit,
    onGoToTutor: (tutorId: Int) -> Unit,
    onRebook: (tutorId: Int) -> Unit,
    viewModel: LessonDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lessonId) { viewModel.load(lessonId) }

    LaunchedEffect(state.actionSuccess, state.actionError) {
        val msg = state.actionSuccess ?: state.actionError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
        viewModel.clearMessages()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = state.lesson?.subjectName ?: "Lekcja",
                subtitle = "Szczegóły lekcji",
                background = HeaderBackground.Diagonal(
                    listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
                ),
                onBack = onBack,
            )

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.error != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.error!!,
                        style = typography.bodyLarge,
                        color = colorScheme.error,
                        modifier = Modifier.padding(24.dp),
                    )
                }

                state.lesson != null -> {
                    val lesson = state.lesson!!
                    val userRole = state.currentUserRole ?: UserRole.STUDENT
                    val thirtyMinPast = viewModel.isThirtyMinutesAfterStart(lesson)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Info card
                        InfoCard(lesson = lesson, userRole = userRole)

                        // Notes
                        NotesSection(
                            lesson = lesson,
                            userRole = userRole,
                            isSaving = state.isSaving,
                            onSaveStudentNotes = { notes ->
                                viewModel.saveStudentNotes(lesson.id, notes)
                            },
                            onSaveTutorNotes = { notes ->
                                viewModel.saveTutorNotes(lesson.id, notes)
                            },
                        )

                        // Actions
                        ActionsSection(
                            lesson = lesson,
                            userRole = userRole,
                            isSaving = state.isSaving,
                            thirtyMinPast = thirtyMinPast,
                            onChangeStatus = { status ->
                                viewModel.changeStatus(lesson.id, status)
                            },
                            onMarkPaid = { viewModel.markPaid(lesson.id) },
                            onGoToTutor = { onGoToTutor(lesson.tutorId) },
                            onRebook = { onRebook(lesson.tutorId) },
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}