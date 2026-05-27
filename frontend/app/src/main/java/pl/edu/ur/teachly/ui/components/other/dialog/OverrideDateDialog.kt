package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.components.other.formatDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverrideDateDialog(
    onDismiss: () -> Unit,
    onSave: (date: String, timeFrom: String?, timeTo: String?) -> Unit,
) {
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
            .toEpochMilli(),
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
            },
        ) { DatePicker(state = datePickerState) }
    }

    if (showFromPicker) {
        SlottedTimePickerDialog(
            title = "Godzina rozpoczęcia",
            initialHour = fromHour,
            initialMinute = fromMinute,
            onDismiss = { showFromPicker = false },
            onConfirm = { h, m -> fromHour = h; fromMinute = m; showFromPicker = false },
        )
    }

    if (showToPicker) {
        SlottedTimePickerDialog(
            title = "Godzina zakończenia",
            initialHour = toHour,
            initialMinute = toMinute,
            onDismiss = { showToPicker = false },
            onConfirm = { h, m -> toHour = h; toMinute = m; showToPicker = false },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj niedostępność") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box {
                    OutlinedTextField(
                        value = runCatching { formatDate(LocalDate.parse(selectedDate)) }.getOrDefault(
                            selectedDate
                        ),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Data") },
                        trailingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Wybierz datę")
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true })
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Cały dzień", style = typography.bodyMedium, color = colorScheme.onSurface)
                    Switch(checked = allDay, onCheckedChange = { allDay = it })
                }

                if (!allDay) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = timeFrom,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Od") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null
                                    )
                                },
                                isError = !rangeValid,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showFromPicker = true })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = timeTo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Do") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null
                                    )
                                },
                                isError = !rangeValid,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showToPicker = true })
                        }
                    }
                    if (!rangeValid) {
                        Text(
                            text = "Godzina zakończenia musi być późniejsza od rozpoczęcia",
                            style = typography.bodySmall,
                            color = colorScheme.error,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        selectedDate,
                        if (allDay) null else timeFrom,
                        if (allDay) null else timeTo
                    )
                },
                enabled = canSave,
            ) { Text("Dodaj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}
