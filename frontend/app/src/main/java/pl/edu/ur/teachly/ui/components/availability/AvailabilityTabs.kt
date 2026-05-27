package pl.edu.ur.teachly.ui.components.availability

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse
import pl.edu.ur.teachly.ui.components.other.EmptyListState
import pl.edu.ur.teachly.ui.components.other.cards.DayCard
import pl.edu.ur.teachly.ui.models.DAY_NAMES

@Composable
fun WeeklyTab(
    recurring: List<TutorAvailabilityRecurringResponse>,
    onAddSlot: (Int) -> Unit,
    onDeleteSlot: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items((1..7).toList(), key = { it }) { day ->
            val slots = recurring
                .filter { it.dayOfWeek == day }
                .sortedBy { it.timeFrom }
            DayCard(
                dayName = DAY_NAMES[day]!!,
                slots = slots,
                onAdd = { onAddSlot(day) },
                onDelete = onDeleteSlot,
            )
        }
    }
}

@Composable
fun OverridesTab(
    overrides: List<TutorAvailabilityOverrideResponse>,
    onDeleteOverride: (Int) -> Unit,
) {
    if (overrides.isEmpty()) {
        EmptyListState(message = "Brak zdefiniowanej niedostępności")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                overrides.sortedBy { it.overrideDate },
                key = { it.id },
            ) { override ->
                OverrideItem(
                    override = override,
                    onDelete = { onDeleteOverride(override.id) },
                )
            }
        }
    }
}