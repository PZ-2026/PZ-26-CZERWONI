package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse

private fun hasOverlap(
    timeFrom: String,
    timeTo: String,
    slots: List<TutorAvailabilityRecurringResponse>,
): Boolean = slots.any { slot ->
    timeFrom < slot.timeTo.take(5) && timeTo > slot.timeFrom.take(5)
}

@Composable
fun AvailabilityTimeRangeDialog(
    dayName: String,
    existingSlots: List<TutorAvailabilityRecurringResponse>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var fromHour by remember { mutableIntStateOf(9) }
    var fromMinute by remember { mutableIntStateOf(0) }
    var toHour by remember { mutableIntStateOf(10) }
    var toMinute by remember { mutableIntStateOf(0) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    val timeFrom = "%02d:%02d".format(fromHour, fromMinute)
    val timeTo = "%02d:%02d".format(toHour, toMinute)
    val rangeValid = timeFrom < timeTo
    val overlaps = rangeValid && hasOverlap(timeFrom, timeTo, existingSlots)
    val canSave = rangeValid && !overlaps

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
        title = { Text("Dodaj dostępność — $dayName") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            isError = !rangeValid || overlaps,
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
                } else if (overlaps) {
                    Text(
                        text = "Ten przedział pokrywa się z istniejącym",
                        style = typography.bodySmall,
                        color = colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(timeFrom, timeTo) }, enabled = canSave) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}
