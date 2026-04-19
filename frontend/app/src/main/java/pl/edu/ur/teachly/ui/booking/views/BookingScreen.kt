package pl.edu.ur.teachly.ui.booking.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.booking.viewmodels.BookingViewModel
import pl.edu.ur.teachly.ui.components.booking.BookingSummaryBar
import pl.edu.ur.teachly.ui.components.booking.DayPicker
import pl.edu.ur.teachly.ui.components.booking.DurationPicker
import pl.edu.ur.teachly.ui.components.booking.FormatPicker
import pl.edu.ur.teachly.ui.components.booking.SubjectPicker
import pl.edu.ur.teachly.ui.components.booking.TimeSlotGrid
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.HeaderBackground

@Composable
fun BookingScreen(
    tutorId: String,
    onBack: () -> Unit,
    onConfirm: (tutorName: String, subjectName: String, lessonDate: String, timeFrom: String, timeTo: String, amount: String) -> Unit,
    viewModel: BookingViewModel = koinViewModel(),
) {
    val tutorIdInt = tutorId.toIntOrNull() ?: 0
    LaunchedEffect(tutorIdInt) { viewModel.load(tutorIdInt) }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        AppHeader(
            title = stringResource(R.string.booking_title),
            subtitle = state.tutor?.let {
                stringResource(
                    R.string.tutor_name,
                    it.firstName,
                    it.lastName
                )
            } ?: "",
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
            onBack = onBack,
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.error!!,
                    style = typography.bodyMedium,
                    color = colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }

            else -> {
                val availableSlots = viewModel.availableSlotsForSelectedDay

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    DayPicker(
                        days = state.calendarDays.map { it.first },
                        selectedIndex = state.selectedDayIndex,
                        onSelect = { index -> viewModel.onDaySelect(index) },
                    )
                    Spacer(Modifier.height(24.dp))
                    DurationPicker(
                        selected = state.selectedDuration,
                        onSelect = viewModel::onDurationSelect,
                    )
                    Spacer(Modifier.height(24.dp))
                    if (state.tutorSubjects.isNotEmpty()) {
                        SubjectPicker(
                            subjects = state.tutorSubjects.map { it.subjectName },
                            selectedIndex = state.selectedSubjectIndex,
                            onSelect = viewModel::onSubjectSelect,
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                    if (state.availableFormats.size > 1) {
                        FormatPicker(
                            formats = state.availableFormats,
                            selectedFormat = state.selectedFormat,
                            onSelect = viewModel::onFormatSelect,
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                    TimeSlotGrid(
                        availableSlots = availableSlots,
                        selectedSlot = state.selectedSlot,
                        onSelect = viewModel::onSlotSelect,
                    )
                    if (state.submitError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = state.submitError!!,
                            style = typography.bodySmall,
                            color = colorScheme.error,
                        )
                    }
                    Spacer(Modifier.height(80.dp))
                }

                BookingSummaryBar(
                    tutorName = state.tutor?.let { "${it.firstName} ${it.lastName}" } ?: "",
                    pricePerHour = state.tutor?.hourlyRate?.toInt() ?: 0,
                    selectedDay = state.calendarDays.getOrNull(state.selectedDayIndex)?.first,
                    selectedSlot = state.selectedSlot,
                    selectedDuration = state.selectedDuration,
                    isSubmitting = state.isSubmitting,
                    onConfirm = {
                        viewModel.confirmBooking { tutorName, subjectName, lessonDate, timeFrom, timeTo, amount ->
                            onConfirm(tutorName, subjectName, lessonDate, timeFrom, timeTo, amount)
                        }
                    },
                )
            }
        }
    }
}
