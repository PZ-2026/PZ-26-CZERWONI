package pl.edu.ur.teachly.ui.components.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.ui.components.other.SectionLabel
import pl.edu.ur.teachly.ui.models.CalendarDay
import pl.edu.ur.teachly.ui.models.DURATION_OPTIONS

@Composable
fun DayPicker(
    days: List<CalendarDay>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    SectionLabel(text = stringResource(R.string.booking_section_day))
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEachIndexed { index, day ->
            val isSelected = selectedIndex == index
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) colorScheme.primary else colorScheme.surfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(index) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = day.shortName,
                    style = typography.labelSmall,
                    color = if (isSelected) colorScheme.onPrimary.copy(alpha = 0.7f)
                    else colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = day.dayNumber,
                    style = typography.labelMedium,
                    color = if (isSelected) colorScheme.onPrimary
                    else colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun DurationPicker(selected: Int, onSelect: (Int) -> Unit) {
    SectionLabel(text = stringResource(R.string.booking_section_duration))
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DURATION_OPTIONS.forEach { duration ->
            val isSelected = selected == duration
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(duration) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant,
                border = if (isSelected) BorderStroke(
                    2.dp,
                    colorScheme.primary
                ) else null,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$duration min",
                        style = typography.labelMedium,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectPicker(
    subjects: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    SectionLabel(text = stringResource(R.string.subject))
    Spacer(Modifier.height(10.dp))

    var expanded by remember { mutableStateOf(false) }
    val selectedLabel =
        subjects.getOrElse(selectedIndex) { stringResource(R.string.choose_subject) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            subjects.forEachIndexed { index, subject ->
                DropdownMenuItem(
                    text = { Text(subject) },
                    onClick = { onSelect(index); expanded = false },
                )
            }
        }
    }
}

@Composable
fun SlotLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            label,
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun TimeSlotGrid(
    availableSlots: List<String>,
    selectedSlot: String?,
    onSelect: (String) -> Unit,
) {
    SectionLabel(text = stringResource(R.string.booking_section_time))
    Spacer(Modifier.height(10.dp))

    if (availableSlots.isEmpty()) {
        Text(
            text = stringResource(R.string.no_available_slots_this_day),
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        return
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 12.dp),
    ) {
        SlotLegendItem(
            color = colorScheme.primary,
            label = stringResource(R.string.legend_selected)
        )
        SlotLegendItem(
            color = colorScheme.surface,
            label = stringResource(R.string.legend_free)
        )
    }

    val columns = 4
    availableSlots.chunked(columns).forEach { rowSlots ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rowSlots.forEach { slot ->
                val isSelected = slot == selectedSlot
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) colorScheme.primary else colorScheme.surface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelect(slot) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = slot,
                        style = typography.labelMedium,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface,
                    )
                }
            }
            repeat(columns - rowSlots.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
fun FormatPicker(
    formats: List<LessonFormat>,
    selectedFormat: LessonFormat?,
    onSelect: (LessonFormat) -> Unit,
) {
    SectionLabel(text = stringResource(R.string.lesson_format))
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        formats.forEach { format ->
            val isSelected = format == selectedFormat
            val label =
                if (format == LessonFormat.ONLINE) stringResource(R.string.online) else stringResource(
                    R.string.in_person
                )
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(format) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant,
                border = if (isSelected) BorderStroke(2.dp, colorScheme.primary) else null,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = typography.labelMedium,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
