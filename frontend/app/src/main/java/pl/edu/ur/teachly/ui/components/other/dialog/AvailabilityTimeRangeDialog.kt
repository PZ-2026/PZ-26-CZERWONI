package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse

private fun hasOverlap(timeFrom: String, timeTo: String, slots: List<TutorAvailabilityRecurringResponse>): Boolean =
    slots.any { slot ->
        timeFrom < slot.timeTo.take(5) && timeTo > slot.timeFrom.take(5)
    }

@Composable
fun AvailabilityTimeRangeDialog(
    dayName: String,
    existingSlots: List<TutorAvailabilityRecurringResponse>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
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
            title = stringResource(R.string.time_picker_start),
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
            title = stringResource(R.string.time_picker_end),
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
        title = stringResource(R.string.dialog_add_availability),
        subtitle = dayName,
        onDismiss = onDismiss,
        onConfirm = { onSave(timeFrom, timeTo) },
        confirmText = stringResource(R.string.btn_add),
        confirmEnabled = canSave
    ) {
        DialogSectionCard {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DialogPickerField(
                    value = timeFrom,
                    label = stringResource(R.string.field_from),
                    onClick = { showFromPicker = true },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Schedule, null) },
                    isError = !rangeValid
                )
                DialogPickerField(
                    value = timeTo,
                    label = stringResource(R.string.field_to),
                    onClick = { showToPicker = true },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Schedule, null) },
                    isError = !rangeValid || overlaps
                )
            }
            if (!rangeValid) {
                DialogErrorText(stringResource(R.string.error_time_range_invalid))
            } else if (overlaps) {
                DialogErrorText(stringResource(R.string.error_time_range_overlap))
            }
        }
    }
}
