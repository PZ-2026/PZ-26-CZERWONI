package pl.edu.ur.teachly.ui.booking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonRequest
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class TimeSlotUI(val time: String, val isAvailable: Boolean)

data class BookingUiState(
    val tutor: TutorResponse? = null,
    val calendarDays: List<Pair<CalendarDay, LocalDate>> = emptyList(),
    val timetableByDate: Map<String, List<TimeSlotUI>> = emptyMap(),
    val tutorSubjects: List<TutorSubjectResponse> = emptyList(),
    val availableFormats: List<LessonFormat> = emptyList(),
    val selectedDayIndex: Int = 0,
    val selectedSlot: String? = null,
    val selectedDuration: Int = 60,
    val selectedSubjectIndex: Int = 0,
    val selectedFormat: LessonFormat? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val submitError: String? = null,
)

class BookingViewModel(
    private val tutorRepository: TutorRepository,
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingUiState())
    val state: StateFlow<BookingUiState> = _state.asStateFlow()

    fun load(tutorId: Int) {
        viewModelScope.launch {
            _state.value = BookingUiState(isLoading = true)

            val days = buildCalendarDays()
            _state.update { it.copy(calendarDays = days) }

            // Load tutor and subjects in parallel
            tutorRepository.getTutorById(tutorId).fold(
                onSuccess = { tutor ->
                    val formats = buildList {
                        if (tutor.offersOnline) add(LessonFormat.ONLINE)
                        if (tutor.offersInPerson) add(LessonFormat.IN_PERSON)
                    }
                    _state.update {
                        it.copy(
                            tutor = tutor,
                            availableFormats = formats,
                            selectedFormat = formats.firstOrNull(),
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                    return@launch
                },
            )

            tutorRepository.getTutorSubjects(tutorId).fold(
                onSuccess = { subjects -> _state.update { it.copy(tutorSubjects = subjects) } },
                onFailure = {},
            )

            // Load timetable for next 7 days
            val from = days.first().second.toString()
            val to = days.last().second.toString()
            tutorRepository.getTimetable(tutorId, from, to).fold(
                onSuccess = { timetable ->
                    val byDate = timetable
                        .groupBy { it.date }
                        .mapValues { entry ->
                            val daySlots = entry.value.flatMap { it.availableSlots }
                            if (daySlots.isEmpty()) emptyList<TimeSlotUI>()
                            else {
                                val minTime = daySlots.minOf { LocalTime.parse(it.timeFrom) }
                                val maxTime = daySlots.maxOf { LocalTime.parse(it.timeTo) }

                                generateSequence(minTime) { current ->
                                    val next = current.plusMinutes(30)
                                    if (next < maxTime) next else null
                                }.map { time ->
                                    val tStr = time.toString().take(5)
                                    val isAvail = daySlots.any { slot ->
                                        val s = LocalTime.parse(slot.timeFrom)
                                        val e = LocalTime.parse(slot.timeTo)
                                        !time.isBefore(s) && !time.plusMinutes(30).isAfter(e)
                                    }
                                    TimeSlotUI(tStr, isAvail)
                                }.toList()
                            }
                        }
                    _state.update { it.copy(timetableByDate = byDate, isLoading = false) }
                },
                onFailure = { _state.update { it.copy(isLoading = false) } },
            )
        }
    }

    fun onDaySelect(index: Int) {
        _state.update { it.copy(selectedDayIndex = index, selectedSlot = null) }
    }

    fun onSlotSelect(slot: String) {
        _state.update { it.copy(selectedSlot = slot, submitError = null) }
    }

    fun onDurationSelect(duration: Int) {
        _state.update { it.copy(selectedDuration = duration, selectedSlot = null) }
    }

    fun onSubjectSelect(index: Int) {
        _state.update { it.copy(selectedSubjectIndex = index) }
    }

    fun onFormatSelect(format: LessonFormat) {
        _state.update { it.copy(selectedFormat = format) }
    }

    val availableSlotsForSelectedDay: List<TimeSlotUI>
        get() {
            val date = _state.value.calendarDays.getOrNull(_state.value.selectedDayIndex)?.second
                ?: return emptyList()
            val slotsForDay = _state.value.timetableByDate[date.toString()] ?: return emptyList()

            val duration = _state.value.selectedDuration
            val slotsNeeded = duration / 30

            return slotsForDay.mapIndexed { index, slot ->
                if (!slot.isAvailable) return@mapIndexed slot

                val allFit = (0 until slotsNeeded).all { i ->
                    val futureIndex = index + i
                    futureIndex < slotsForDay.size && slotsForDay[futureIndex].isAvailable
                }

                slot.copy(isAvailable = allFit)
            }
        }

    fun isDurationAvailable(duration: Int): Boolean {
        val st = _state.value
        val slot = st.selectedSlot ?: return true // all allowed if no slot selected
        val dayDate = st.calendarDays.getOrNull(st.selectedDayIndex)?.second ?: return true
        val slotsForDay = st.timetableByDate[dayDate.toString()] ?: return false

        val startTime = LocalTime.parse(slot)
        val slotsNeeded = duration / 30

        return (0 until slotsNeeded).all { i ->
            val slotTime = startTime.plusMinutes(30L * i).toString().take(5)
            slotsForDay.find { it.time == slotTime }?.isAvailable == true
        }
    }

    fun confirmBooking(
        onSuccess: (tutorName: String, subjectName: String, lessonDate: String, timeFrom: String, timeTo: String, format: String, amount: String) -> Unit
    ) {
        val st = _state.value
        val tutor = st.tutor ?: return
        val slot = st.selectedSlot ?: return
        val dayDate = st.calendarDays.getOrNull(st.selectedDayIndex)?.second ?: return
        val subject = st.tutorSubjects.getOrNull(st.selectedSubjectIndex) ?: return
        val format = st.selectedFormat ?: return

        // Validate consecutive slots
        val available =
            st.timetableByDate[dayDate.toString()]?.filter { it.isAvailable }?.map { it.time }
                ?: emptyList()
        val startTime = LocalTime.parse(slot)
        val slotsNeeded = st.selectedDuration / 30
        val allAvailable = (0 until slotsNeeded).all { i ->
            val slotTime = startTime.plusMinutes(30L * i)
            slotTime.toString().take(5) in available
        }

        if (!allAvailable) {
            _state.update { it.copy(submitError = "Wybrany czas jest niedostępny dla tej długości lekcji") }
            return
        }

        viewModelScope.launch {
            val userId = tokenManager.userIdFlow.first() ?: return@launch
            _state.update { it.copy(isSubmitting = true, submitError = null) }

            val endTime = startTime.plusMinutes(st.selectedDuration.toLong())
            val amount = tutor.hourlyRate * st.selectedDuration / 60.0
            val request = LessonRequest(
                tutorId = tutor.id,
                subjectId = subject.subjectId,
                lessonDate = dayDate.toString(),
                timeFrom = "$slot:00",
                timeTo = "${endTime.toString().take(5)}:00",
                format = format,
                lessonStatus = LessonStatus.PENDING,
                studentNotes = null,
                amount = amount,
            )

            lessonRepository.createLesson(userId, request).fold(
                onSuccess = { lesson ->
                    _state.update { it.copy(isSubmitting = false) }
                    val formatLabel = when (format) {
                        LessonFormat.ONLINE -> "Online"
                        LessonFormat.IN_PERSON -> "Stacjonarnie"
                    }
                    onSuccess(
                        "${tutor.firstName} ${tutor.lastName}",
                        lesson.subjectName,
                        lesson.lessonDate,
                        lesson.timeFrom.take(5),
                        lesson.timeTo.take(5),
                        formatLabel,
                        "%.2f".format(lesson.amount),
                    )
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, submitError = e.message) }
                },
            )
        }
    }

    private fun buildCalendarDays(): List<Pair<CalendarDay, LocalDate>> {
        val today = LocalDate.now()
        return (0..13).map { offset ->
            val date = today.plusDays(offset.toLong())
            val shortName = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "Pon"
                DayOfWeek.TUESDAY -> "Wt"
                DayOfWeek.WEDNESDAY -> "Śr"
                DayOfWeek.THURSDAY -> "Czw"
                DayOfWeek.FRIDAY -> "Pt"
                DayOfWeek.SATURDAY -> "Sob"
                DayOfWeek.SUNDAY -> "Nd"
            }
            CalendarDay(shortName, date.dayOfMonth.toString()) to date
        }
    }
}
