package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import pl.edu.ur.teachly.ui.components.other.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverrideDateDialog(onDismiss: () -> Unit, onSave: (date: String, timeFrom: String?, timeTo: String?) -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    var allDay by remember { mutableStateOf(true) }
    var fromHour by remember { mutableIntStateOf(8) }
    var fromMinute by remember { mutableIntStateOf(0) }
    var toHour by remember { mutableIntStateOf(16) }
    var toMinute by remember { mutableIntStateOf(0) }

    val timeFrom = "%02d:%02d".format(fromHour, fromMinute)
    val timeTo = "%02d:%02d".format(toHour, toMinute)
    val rangeValid = timeFrom < timeTo
    val canSave = allDay || rangeValid

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showFromPicker) {
        SlottedTimePickerDialog(
            title = "Godzina rozpoczęcia",
            initialHour = fromHour,
            initialMinute = fromMinute,
            onDismiss = { showFromPicker = false },
            onConfirm = { h, m ->
                fromHour = h
                fromMinute = m
                showFromPicker = false
            }
        )
    }

    if (showToPicker) {
        SlottedTimePickerDialog(
            title = "Godzina zakończenia",
            initialHour = toHour,
            initialMinute = toMinute,
            onDismiss = { showToPicker = false },
            onConfirm = { h, m ->
                toHour = h
                toMinute = m
                showToPicker = false
            }
        )
    }

    AppFormDialog(
        title = "Dodaj niedostępność",
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                selectedDate,
                if (allDay) null else timeFrom,
                if (allDay) null else timeTo
            )
        },
        confirmText = "Dodaj",
        confirmEnabled = canSave
    ) {
        DialogSectionCard {
            DialogPickerField(
                value = runCatching { formatDate(LocalDate.parse(selectedDate)) }.getOrDefault(selectedDate),
                label = "Data",
                onClick = { showDatePicker = true },
                trailingIcon = Icons.Default.CalendarMonth,
                trailingIconDescription = "Wybierz datę"
            )
        }

        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))

        DialogSectionCard(title = "Godziny") {
            DialogSwitchRow("Cały dzień", allDay) { allDay = it }

            if (!allDay) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogPickerField(
                        value = timeFrom,
                        label = "Od",
                        onClick = { showFromPicker = true },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        isError = !rangeValid
                    )
                    DialogPickerField(
                        value = timeTo,
                        label = "Do",
                        onClick = { showToPicker = true },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        isError = !rangeValid
                    )
                }
                if (!rangeValid) {
                    DialogErrorText("Godzina zakończenia musi być późniejsza od rozpoczęcia")
                }
            }
        }
    }
}
