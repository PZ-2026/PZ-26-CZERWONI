package pl.edu.ur.teachly.ui.lesson.views

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.LessonStatusBadge
import pl.edu.ur.teachly.ui.lesson.viewmodels.LessonDetailViewModel
import java.time.LocalTime

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
                    val role = state.currentUserRole
                    val isStudent = role != "TUTOR"
                    val isTutor = role == "TUTOR"
                    val thirtyMinPast = viewModel.isThirtyMinutesAfterStart(lesson)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Info card
                        InfoCard(lesson = lesson, isStudent = isStudent)

                        // Notes
                        NotesSection(
                            lesson = lesson,
                            isStudent = isStudent,
                            isTutor = isTutor,
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
                            isStudent = isStudent,
                            isTutor = isTutor,
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

@Composable
private fun InfoCard(lesson: LessonResponse, isStudent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = lesson.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                LessonStatusBadge(lesson.lessonStatus)
            }

            // Person
            val personLabel = if (isStudent) "Korepetytor" else "Uczeń"
            val personName = if (isStudent)
                "${lesson.tutorFirstName} ${lesson.tutorLastName}".trim()
            else
                "${lesson.studentFirstName} ${lesson.studentLastName}".trim()
            DetailRow(Icons.Default.Person, "$personLabel: $personName")

            // Date
            DetailRow(Icons.Default.CalendarMonth, lesson.lessonDate)

            // Time
            val endTime = try {
                LocalTime.parse(lesson.timeFrom).plusMinutes(
                    java.time.Duration.between(
                        LocalTime.parse(lesson.timeFrom),
                        LocalTime.parse(lesson.timeTo),
                    ).toMinutes()
                ).toString().take(5)
            } catch (e: Exception) {
                lesson.timeTo.take(5)
            }
            val durationMin = try {
                java.time.Duration.between(
                    LocalTime.parse(lesson.timeFrom),
                    LocalTime.parse(lesson.timeTo)
                ).toMinutes()
            } catch (e: Exception) {
                0L
            }
            DetailRow(
                Icons.Default.Schedule,
                "${lesson.timeFrom.take(5)} – $endTime | ($durationMin min)"
            )

            // Format
            val formatLabel = when (lesson.format) {
                LessonFormat.ONLINE -> "Online"
                LessonFormat.IN_PERSON -> "Stacjonarnie"
            }
            DetailRow(Icons.Default.School, formatLabel)

            // Amount + payment status
            val payLabel = when (lesson.paymentStatus) {
                PaymentStatus.PAID -> "Opłacone"
                PaymentStatus.PENDING -> "Nieopłacone"
                PaymentStatus.CANCELLED -> "Anulowane"
            }
            DetailRow(Icons.Default.CreditCard, "${lesson.amount} zł | $payLabel")
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = colorScheme.primary)
        Text(text = text, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    }
}

// Notes section
@Composable
private fun NotesSection(
    lesson: LessonResponse,
    isStudent: Boolean,
    isTutor: Boolean,
    isSaving: Boolean,
    onSaveStudentNotes: (String) -> Unit,
    onSaveTutorNotes: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Notatki",
                style = typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
            )

            // Student notes
            NoteField(
                label = "Notatki ucznia",
                initialValue = lesson.studentNotes ?: "",
                editable = isStudent,
                isSaving = isSaving,
                onSave = onSaveStudentNotes,
            )

            // Tutor notes
            NoteField(
                label = "Notatki korepetytora",
                initialValue = lesson.tutorNotes ?: "",
                editable = isTutor,
                isSaving = isSaving,
                onSave = onSaveTutorNotes,
            )
        }
    }
}

@Composable
private fun NoteField(
    label: String,
    initialValue: String,
    editable: Boolean,
    isSaving: Boolean,
    onSave: (String) -> Unit,
) {
    var text by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    val isDirty = text != initialValue

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = typography.labelMedium,
            color = colorScheme.onSurfaceVariant,
        )
        if (editable) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Wpisz notatki…") },
            )
            if (isDirty) {
                Button(
                    onClick = { onSave(text) },
                    enabled = !isSaving,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(if (isSaving) "Zapisywanie…" else "Zapisz")
                }
            }
        } else {
            if (initialValue.isBlank()) {
                Text(
                    text = "Brak notatek",
                    style = typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.4f),
                )
            } else {
                Text(
                    text = initialValue,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// Actions section
@Composable
private fun ActionsSection(
    lesson: LessonResponse,
    isStudent: Boolean,
    isTutor: Boolean,
    isSaving: Boolean,
    thirtyMinPast: Boolean,
    onChangeStatus: (LessonStatus) -> Unit,
    onMarkPaid: () -> Unit,
    onGoToTutor: () -> Unit,
    onRebook: () -> Unit,
) {
    val status = lesson.lessonStatus
    var pendingConfirm by remember { mutableStateOf<ConfirmConfig?>(null) }

    pendingConfirm?.let { config ->
        ConfirmDialog(config = config, onDismiss = { pendingConfirm = null })
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        if (isStudent) {
            // Go to tutor profile (always)
            OutlinedButton(
                onClick = onGoToTutor,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Profil korepetytora")
            }

            // Rebook (if cancelled or completed)
            if (status == LessonStatus.CANCELLED || status == LessonStatus.COMPLETED) {
                Button(
                    onClick = onRebook,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Zarezerwuj ponownie")
                }
            }

            // Cancel (if pending or confirmed)
            if (status == LessonStatus.PENDING || status == LessonStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Anuluj lekcję",
                            message = "Czy na pewno chcesz anulować tę lekcję? Tej operacji nie można cofnąć.",
                            confirmLabel = "Anuluj lekcję",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Anuluj lekcję")
                }
            }
        }

        if (isTutor) {
            // Accept/reject (if pending)
            if (status == LessonStatus.PENDING) {
                Button(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Zaakceptuj lekcję",
                            message = "Czy na pewno chcesz zaakceptować tę lekcję?",
                            confirmLabel = "Zaakceptuj",
                            action = { onChangeStatus(LessonStatus.CONFIRMED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Zaakceptuj")
                }
                Spacer(Modifier.height(2.dp))
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Odrzuć lekcję",
                            message = "Czy na pewno chcesz odrzucić tę lekcję?",
                            confirmLabel = "Odrzuć",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.error,
                    ),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Odrzuć")
                }
            }

            // Cancel (if confirmed)
            if (status == LessonStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Anuluj lekcję",
                            message = "Czy na pewno chcesz anulować potwierdzoną lekcję?",
                            confirmLabel = "Anuluj lekcję",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Anuluj lekcję")
                }
            }

            // Mark completed. Must be confirmed + 30min past start
            if (status == LessonStatus.CONFIRMED && thirtyMinPast) {
                Button(
                    onClick = { onChangeStatus(LessonStatus.COMPLETED) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Oznacz jako zakończoną")
                }
            }

            // Mark paid. Must be completed or confirmed + 30min past AND not already paid
            val canMarkPaid = lesson.paymentStatus == PaymentStatus.PENDING &&
                    (status == LessonStatus.COMPLETED || (status == LessonStatus.CONFIRMED && thirtyMinPast))
            if (canMarkPaid) {
                Button(
                    onClick = onMarkPaid,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.tertiary,
                        contentColor = colorScheme.onTertiary,
                    ),
                ) {
                    Text("Oznacz jako opłaconą")
                }
            }
        }
    }
}

private data class ConfirmConfig(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val destructive: Boolean = false,
    val action: () -> Unit,
)

// Confirm dialog
@Composable
private fun ConfirmDialog(config: ConfirmConfig, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(config.title) },
        text = { Text(config.message) },
        confirmButton = {
            Button(
                onClick = { config.action(); onDismiss() },
                colors = if (config.destructive)
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError,
                    )
                else ButtonDefaults.buttonColors(),
            ) {
                Text(config.confirmLabel)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}