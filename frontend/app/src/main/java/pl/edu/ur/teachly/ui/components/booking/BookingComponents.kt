package pl.edu.ur.teachly.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R

@Composable
fun DayPicker(selectedIndex: Int, onSelect: (Int) -> Unit) {
    SectionLabel(text = stringResource(R.string.booking_section_day))
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CALENDAR_DAYS.forEachIndexed { index, day ->
            val isSelected = selectedIndex == index
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(index) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = day.shortName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = day.dayNumber,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
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
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                ) else null,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$duration min",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun SlotLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(modifier = Modifier
            .size(10.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TimeSlotGrid(selectedSlot: String?, onSelect: (String) -> Unit) {
    SectionLabel(text = stringResource(R.string.booking_section_time))
    Spacer(Modifier.height(10.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        SlotLegendItem(
            color = MaterialTheme.colorScheme.primary,
            label = stringResource(R.string.legend_selected)
        )
        SlotLegendItem(
            color = MaterialTheme.colorScheme.outline,
            label = stringResource(R.string.legend_booked)
        )
        SlotLegendItem(
            color = MaterialTheme.colorScheme.surface,
            label = stringResource(R.string.legend_free)
        )
    }

    val columns = 4
    ALL_TIME_SLOTS.chunked(columns).forEach { rowSlots ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowSlots.forEach { slot ->
                val isBooked = slot in BOOKED_SLOTS
                val isSelected = slot == selectedSlot
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isBooked -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .then(
                            if (!isBooked) Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onSelect(slot) }
                            else Modifier
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = slot,
                        style = MaterialTheme.typography.labelMedium,
                        textDecoration = if (isBooked) TextDecoration.LineThrough else TextDecoration.None,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isBooked -> MaterialTheme.colorScheme.outline
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
            repeat(columns - rowSlots.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}
