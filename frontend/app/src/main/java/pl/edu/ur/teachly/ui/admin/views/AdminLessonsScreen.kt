package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.FilterChips
import pl.edu.ur.teachly.ui.components.other.badges.LessonStatusBadge
import pl.edu.ur.teachly.ui.components.other.badges.PaymentStatusBadge
import pl.edu.ur.teachly.ui.components.other.cards.CardInfoRow
import pl.edu.ur.teachly.ui.components.other.dialog.DialogChipRow
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSectionLabel

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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = lesson.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edytuj",
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Korepetytor: ${lesson.tutorFirstName} ${lesson.tutorLastName}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.School,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Uczeń: ${lesson.studentFirstName} ${lesson.studentLastName}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = lesson.lessonDate,
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Schedule,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "${lesson.timeFrom}–${lesson.timeTo}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Payments,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "${lesson.amount} PLN",
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LessonStatusBadge(lesson.lessonStatus)
                PaymentStatusBadge(lesson.paymentStatus)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminLessonEditDialog(
    lesson: LessonResponse,
    onDismiss: () -> Unit,
    onSave: (AdminLessonUpdateRequest) -> Unit,
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

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimeFromPicker by remember { mutableStateOf(false) }
    var showTimeToPicker by remember { mutableStateOf(false) }

    val initialDateMillis = remember(lesson.lessonDate) {
        runCatching {
            java.time.LocalDate.parse(lesson.lessonDate)
                .atStartOfDay(java.time.ZoneOffset.UTC)
                .toInstant().toEpochMilli()
        }.getOrNull()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    val initialTimeFrom = remember(lesson.timeFrom) {
        runCatching { java.time.LocalTime.parse(lesson.timeFrom) }.getOrNull()
    }
    val timeFromPickerState = rememberTimePickerState(
        initialHour = initialTimeFrom?.hour ?: 8,
        initialMinute = initialTimeFrom?.minute ?: 0,
        is24Hour = true,
    )

    val initialTimeTo = remember(lesson.timeTo) {
        runCatching { java.time.LocalTime.parse(lesson.timeTo) }.getOrNull()
    }
    val timeToPickerState = rememberTimePickerState(
        initialHour = initialTimeTo?.hour ?: 9,
        initialMinute = initialTimeTo?.minute ?: 0,
        is24Hour = true,
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        lessonDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimeFromPicker) {
        TimePickerDialog(
            onDismissRequest = { showTimeFromPicker = false },
            title = { Text("Wybierz godzinę (od)") },
            confirmButton = {
                TextButton(onClick = {
                    timeFrom =
                        "%02d:%02d".format(timeFromPickerState.hour, timeFromPickerState.minute)
                    showTimeFromPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimeFromPicker = false }) { Text("Anuluj") }
            }
        ) {
            TimePicker(state = timeFromPickerState)
        }
    }

    if (showTimeToPicker) {
        TimePickerDialog(
            onDismissRequest = { showTimeToPicker = false },
            title = { Text("Wybierz godzinę (do)") },
            confirmButton = {
                TextButton(onClick = {
                    timeTo = "%02d:%02d".format(timeToPickerState.hour, timeToPickerState.minute)
                    showTimeToPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimeToPicker = false }) { Text("Anuluj") }
            }
        ) {
            TimePicker(state = timeToPickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj lekcję #${lesson.id}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = lessonDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Wybierz datę")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = timeFrom,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Od") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showTimeFromPicker = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = timeTo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Do") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showTimeToPicker = true }
                        )
                    }
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Kwota (PLN)") },
                    leadingIcon = { Icon(Icons.Default.Payments, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                DialogSectionLabel("Format")
                DialogChipRow(
                    entries = LessonFormat.entries,
                    selected = format,
                    onSelect = { format = it },
                    label = { it.name },
                )
                DialogSectionLabel("Status lekcji")
                DialogChipRow(
                    entries = LessonStatus.entries,
                    selected = lessonStatus,
                    onSelect = { lessonStatus = it },
                    label = { it.name },
                )
                DialogSectionLabel("Status płatności")
                DialogChipRow(
                    entries = PaymentStatus.entries,
                    selected = paymentStatus,
                    onSelect = { paymentStatus = it },
                    label = { it.name },
                )
                OutlinedTextField(
                    value = studentNotes,
                    onValueChange = { studentNotes = it },
                    label = { Text("Notatki ucznia") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
                OutlinedTextField(
                    value = tutorNotes,
                    onValueChange = { tutorNotes = it },
                    label = { Text("Notatki korepetytora") },
                    leadingIcon = { Icon(Icons.Default.School, null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
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
                        tutorNotes = tutorNotes.ifBlank { null },
                    )
                )
            }) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
