package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme.colorScheme
import java.time.LocalDate
import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.ui.components.other.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonEditDialog(lesson: LessonResponse, onDismiss: () -> Unit, onSave: (AdminLessonUpdateRequest) -> Unit) {
    var lessonDate by remember { mutableStateOf(lesson.lessonDate) }
    var timeFrom by remember { mutableStateOf(lesson.timeFrom) }
    var timeTo by remember { mutableStateOf(lesson.timeTo) }
    var lessonStatus by remember { mutableStateOf(lesson.lessonStatus) }
    var paymentStatus by remember { mutableStateOf(lesson.paymentStatus) }
    var amount by remember { mutableStateOf(DialogValidation.formatEditableDecimal(lesson.amount)) }
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
        is24Hour = true
    )

    val initialTimeTo = remember(lesson.timeTo) {
        runCatching { java.time.LocalTime.parse(lesson.timeTo) }.getOrNull()
    }
    val timeToPickerState = rememberTimePickerState(
        initialHour = initialTimeTo?.hour ?: 9,
        initialMinute = initialTimeTo?.minute ?: 0,
        is24Hour = true
    )

    val amountError = DialogValidation.lessonAmountError(amount)
    val timeRangeError = DialogValidation.timeRangeError(timeFrom, timeTo)
    val isValid = DialogValidation.isLessonAmountValid(amount) &&
        DialogValidation.isTimeRangeValid(timeFrom, timeTo)

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

    AppFormDialog(
        title = "Edytuj lekcję #${lesson.id}",
        subtitle = "${lesson.subjectName} · ${lesson.tutorFirstName} ${lesson.tutorLastName}",
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                AdminLessonUpdateRequest(
                    lessonDate = lessonDate,
                    timeFrom = timeFrom,
                    timeTo = timeTo,
                    format = format,
                    lessonStatus = lessonStatus,
                    paymentStatus = paymentStatus,
                    amount = DialogValidation.parseDecimal(amount)!!,
                    studentNotes = studentNotes.trim().ifBlank { null },
                    tutorNotes = tutorNotes.trim().ifBlank { null }
                )
            )
        },
        confirmEnabled = isValid
    ) {
        DialogSectionCard(title = "Termin") {
            DialogPickerField(
                value = formatDate(LocalDate.parse(lessonDate)),
                label = "Data",
                onClick = { showDatePicker = true },
                trailingIcon = Icons.Default.CalendarMonth,
                trailingIconDescription = "Wybierz datę"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DialogPickerField(
                    value = timeFrom,
                    label = "Od",
                    onClick = { showTimeFromPicker = true },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                )
                DialogPickerField(
                    value = timeTo,
                    label = "Do",
                    onClick = { showTimeToPicker = true },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                )
            }
            timeRangeError?.let { DialogErrorText(it) }
        }

        DialogSectionCard(title = "Szczegóły") {
            OutlinedTextField(
                value = amount,
                onValueChange = { value ->
                    DialogValidation.filterDecimalInput(value)?.let { amount = it }
                },
                label = { Text("Kwota (PLN)") },
                leadingIcon = { Icon(Icons.Default.Payments, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null,
                supportingText = amountError?.let { error -> { Text(error) } }
            )
            DialogSectionLabel("Format")
            DialogChipRow(
                entries = LessonFormat.entries,
                selected = format,
                onSelect = { format = it },
                label = { it.label }
            )
            DialogSectionLabel("Status lekcji")
            DialogChipRow(
                entries = LessonStatus.entries,
                selected = lessonStatus,
                onSelect = { lessonStatus = it },
                label = { it.label }
            )
            DialogSectionLabel("Status płatności")
            DialogChipRow(
                entries = PaymentStatus.entries,
                selected = paymentStatus,
                onSelect = { paymentStatus = it },
                label = { it.label }
            )
        }

        DialogSectionCard(title = "Notatki") {
            OutlinedTextField(
                value = studentNotes,
                onValueChange = { if (it.length <= DialogValidation.MAX_NOTES_LENGTH) studentNotes = it },
                label = { Text("Notatki ucznia") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                supportingText = {
                    Text(
                        text = "${studentNotes.length}/${DialogValidation.MAX_NOTES_LENGTH}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            )
            OutlinedTextField(
                value = tutorNotes,
                onValueChange = { if (it.length <= DialogValidation.MAX_NOTES_LENGTH) tutorNotes = it },
                label = { Text("Notatki korepetytora") },
                leadingIcon = { Icon(Icons.Default.School, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                supportingText = {
                    Text(
                        text = "${tutorNotes.length}/${DialogValidation.MAX_NOTES_LENGTH}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}
