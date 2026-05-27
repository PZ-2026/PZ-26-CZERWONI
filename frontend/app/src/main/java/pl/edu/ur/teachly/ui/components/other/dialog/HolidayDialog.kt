package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.components.other.formatDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayDialog(
    title: String,
    initialDate: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit,
) {
    var date by remember { mutableStateOf(initialDate) }
    var description by remember { mutableStateOf(initialDescription) }
    var showDatePicker by remember { mutableStateOf(false) }

    val initialDateMillis = remember(initialDate) {
        runCatching {
            LocalDate.parse(initialDate)
                .atStartOfDay(java.time.ZoneOffset.UTC)
                .toInstant().toEpochMilli()
        }.getOrNull()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        date = java.time.Instant.ofEpochMilli(millis)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = formatDate(LocalDate.parse(date)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Wybierz datę")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 100) description = it },
                    label = { Text("Opis (opcjonalnie)") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(date.trim(), description.trim().ifBlank { null }) },
                enabled = date.isNotBlank(),
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
