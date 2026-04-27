package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.ui.components.other.formatDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonEditDialog(
    lesson: LessonResponse,
    onDismiss: () -> Unit,
    onSave: (AdminLessonUpdateRequest) -> Unit,
) {
    var lessonDate by remember { mutableStateOf(lesson.lessonDate) }
    var timeFrom by remember { mutableStateOf(lesson.timeFrom) }
    var timeTo by remember { mutableStateOf(lesson.timeTo) }
    var lessonStatus by remember { mutableStateOf(lesson.lessonStatus) }
    var paymentStatus by remember { mutableStateOf(lesson.paymentStatus) }
    var amount by remember { mutableStateOf("%.2f".format(lesson.amount)) }
    var studentNotes by remember { mutableStateOf(lesson.studentNotes ?: "") }
    var tutorNotes by remember { mutableStateOf(lesson.tutorNotes ?: "") }
    var format by remember { mutableStateOf(lesson.format) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimeFromPicker by remember { mutableStateOf(false) }
    var showTimeToPicker by remember { mutableStateOf(false) }

    val initialDateMillis = remember(lesson.lessonDate) {
        runCatching {
            LocalDate.parse(lesson.lessonDate)
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
                    value = formatDate(LocalDate.parse(lessonDate)),
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
