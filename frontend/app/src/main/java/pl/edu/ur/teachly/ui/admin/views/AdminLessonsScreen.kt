package pl.edu.ur.teachly.ui.admin.views

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminLessonsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.badges.LessonStatusBadge
import pl.edu.ur.teachly.ui.components.other.badges.PaymentStatusBadge

@Composable
fun AdminLessonsScreen(
    viewModel: AdminLessonsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf<LessonResponse?>(null) }

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Surface(color = colorScheme.primary) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Lekcje",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )
                Spacer(Modifier.height(8.dp))
                AdminSearchBar(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = "Szukaj po uczestniku, przedmiocie...",
                )
                Spacer(Modifier.height(8.dp))
                // Status filter chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        null,
                        LessonStatus.PENDING,
                        LessonStatus.CONFIRMED,
                        LessonStatus.COMPLETED,
                        LessonStatus.CANCELLED
                    ).forEach { status ->
                        FilterChip(
                            selected = state.selectedStatus == status,
                            onClick = { viewModel.onStatusFilterChange(status) },
                            label = {
                                Text(
                                    status?.name ?: "Wszystkie",
                                    style = typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.secondary,
                                selectedLabelColor = colorScheme.onSecondary,
                                containerColor = colorScheme.surfaceVariant,
                                labelColor = colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        }

        AdminMessageSnackbars(successMessage = state.successMessage, errorMessage = state.error)

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

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

    // Edit dialog
    showEditDialog?.let { lesson ->
        AdminLessonEditDialog(
            lesson = lesson,
            onDismiss = { showEditDialog = null },
            onSave = { request ->
                viewModel.updateLesson(lesson.id, request)
                showEditDialog = null
            }
        )
    }
}

@Composable
private fun LessonAdminCard(lesson: LessonResponse, onEdit: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    lesson.subjectName,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Korepetytor: ${lesson.tutorFirstName} ${lesson.tutorLastName}",
                    style = typography.bodySmall
                )
                Text(
                    "Uczeń: ${lesson.studentFirstName} ${lesson.studentLastName}",
                    style = typography.bodySmall
                )
                Text(
                    "${lesson.lessonDate}  ${lesson.timeFrom}–${lesson.timeTo}",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LessonStatusBadge(lesson.lessonStatus)
                    PaymentStatusBadge(lesson.paymentStatus)
                }
                Text(
                    "${lesson.amount} PLN",
                    style = typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edytuj", tint = colorScheme.primary)
            }
        }
    }
}

@Composable
private fun AdminLessonEditDialog(
    lesson: LessonResponse,
    onDismiss: () -> Unit,
    onSave: (AdminLessonUpdateRequest) -> Unit
) {
    var lessonDate by remember { mutableStateOf(lesson.lessonDate) }
    var timeFrom by remember { mutableStateOf(lesson.timeFrom) }
    var timeTo by remember { mutableStateOf(lesson.timeTo) }
    var lessonStatus by remember { mutableStateOf(lesson.lessonStatus) }
    var paymentStatus by remember { mutableStateOf(lesson.paymentStatus) }
    var amount by remember { mutableStateOf(lesson.amount.toString()) }
    var studentNotes by remember { mutableStateOf(lesson.studentNotes ?: "") }
    var tutorNotes by remember { mutableStateOf(lesson.tutorNotes ?: "") }
    var format by remember { mutableStateOf(lesson.format) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj lekcję #${lesson.id}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = lessonDate,
                    onValueChange = { lessonDate = it },
                    label = { Text("Data (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = timeFrom,
                        onValueChange = { timeFrom = it },
                        label = { Text("Od") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = timeTo,
                        onValueChange = { timeTo = it },
                        label = { Text("Do") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Kwota (PLN)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Format", style = typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LessonFormat.entries.forEach { f ->
                        FilterChip(
                            selected = format == f,
                            onClick = { format = f },
                            label = { Text(f.name) })
                    }
                }
                Text("Status lekcji", style = typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LessonStatus.entries.forEach { s ->
                        FilterChip(
                            selected = lessonStatus == s,
                            onClick = { lessonStatus = s },
                            label = { Text(s.name, style = typography.labelSmall) })
                    }
                }
                Text("Status płatności", style = typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaymentStatus.entries.forEach { p ->
                        FilterChip(
                            selected = paymentStatus == p,
                            onClick = { paymentStatus = p },
                            label = { Text(p.name, style = typography.labelSmall) })
                    }
                }
                OutlinedTextField(
                    value = studentNotes,
                    onValueChange = { studentNotes = it },
                    label = { Text("Notatki ucznia") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = tutorNotes,
                    onValueChange = { tutorNotes = it },
                    label = { Text("Notatki korepetytora") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    AdminLessonUpdateRequest(
                        lessonDate = lessonDate,
                        timeFrom = timeFrom,
                        timeTo = timeTo,
                        format = format,
                        lessonStatus = lessonStatus,
                        paymentStatus = paymentStatus,
                        amount = amount.toDoubleOrNull() ?: lesson.amount,
                        studentNotes = studentNotes.ifBlank { null },
                        tutorNotes = tutorNotes.ifBlank { null }
                    )
                )
            }) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
