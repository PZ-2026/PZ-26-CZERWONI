package pl.edu.ur.teachly.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.components.BookingHeader
import pl.edu.ur.teachly.ui.components.BookingSummaryBar
import pl.edu.ur.teachly.ui.components.CALENDAR_DAYS
import pl.edu.ur.teachly.ui.components.DayPicker
import pl.edu.ur.teachly.ui.components.DurationPicker
import pl.edu.ur.teachly.ui.components.MOCK_TUTORS
import pl.edu.ur.teachly.ui.components.TimeSlotGrid
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun BookingScreen(
    tutorId: String,
    onBack: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    // TODO: download tutor from ViewModel by tutorId
    val tutor = MOCK_TUTORS.first { it.id.toString() == tutorId }

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableIntStateOf(60) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BookingHeader(tutor = tutor, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            DayPicker(
                selectedIndex = selectedDayIndex,
                onSelect = { index -> selectedDayIndex = index; selectedSlot = null },
            )
            Spacer(Modifier.height(24.dp))
            DurationPicker(selected = selectedDuration, onSelect = { selectedDuration = it })
            Spacer(Modifier.height(24.dp))
            TimeSlotGrid(selectedSlot = selectedSlot, onSelect = { selectedSlot = it })
            Spacer(Modifier.height(80.dp))
        }

        BookingSummaryBar(
            tutor = tutor,
            selectedDayIndex = selectedDayIndex,
            selectedSlot = selectedSlot,
            selectedDuration = selectedDuration,
            onConfirm = {
                val slot = selectedSlot ?: return@BookingSummaryBar
                val day = CALENDAR_DAYS[selectedDayIndex]
                val date = LocalDate.of(2026, 3, day.dayNumber.toInt())
                val time = LocalTime.parse(slot)
                val dateTime = LocalDateTime.of(date, time)
                val formatted = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                // TODO: get real bookingId from API
                onConfirm("mock-booking-id", formatted)
            },
        )
    }
}
