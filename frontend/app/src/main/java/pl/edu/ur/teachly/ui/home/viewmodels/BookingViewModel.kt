package pl.edu.ur.teachly.ui.home.viewmodels

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
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.components.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class BookingUiState(
    val tutor: TutorResponse? = null,
    val calendarDays: List<Pair<CalendarDay, LocalDate>> = emptyList(),
    val timetableByDate: Map<String, List<String>> = emptyMap(),
    val subjects: List<SubjectResponse> = emptyList(),
    val selectedDayIndex: Int = 0,
    val selectedSlot: String? = null,
    val selectedDuration: Int = 60,
    val selectedSubjectIndex: Int = 0,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val submitError: String? = null,
)

class BookingViewModel(
    private val tutorRepository: TutorRepository,
    private val lessonRepository: LessonRepository,
    private val subjectRepository: SubjectRepository,
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
                onSuccess = { tutor -> _state.update { it.copy(tutor = tutor) } },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            error = e.message,
                            isLoading = false
                        )
                    }; return@launch
                },
            )

            subjectRepository.getAllSubjects().fold(
                onSuccess = { subjects -> _state.update { it.copy(subjects = subjects) } },
                onFailure = {},
            )

            // Load timetable for next 7 days
            val from = days.first().second.toString()
            val to = days.last().second.toString()
            tutorRepository.getTimetable(tutorId, from, to).fold(
                onSuccess = { timetable ->
                    val byDate = timetable.associate { day ->
                        day.date to (day.availableSlots?.map { it.timeFrom.take(5) } ?: emptyList())
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

    val availableSlotsForSelectedDay: List<String>
        get() {
            val date = _state.value.calendarDays.getOrNull(_state.value.selectedDayIndex)?.second
                ?: return emptyList()
            return _state.value.timetableByDate[date.toString()] ?: emptyList()
        }

    fun confirmBooking(
        onSuccess: (tutorName: String, subjectName: String, lessonDate: String, timeFrom: String, timeTo: String, amount: String) -> Unit
    ) {
        val st = _state.value
        val tutor = st.tutor ?: return
        val slot = st.selectedSlot ?: return
        val dayDate = st.calendarDays.getOrNull(st.selectedDayIndex)?.second ?: return
        val subject = st.subjects.getOrNull(st.selectedSubjectIndex)

        // Validate consecutive slots
        val available = st.timetableByDate[dayDate.toString()] ?: emptyList()
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
                subjectId = subject?.id ?: 1,
                lessonDate = dayDate.toString(),
                timeFrom = "$slot:00",
                timeTo = "${endTime.toString().take(5)}:00",
                format = LessonFormat.ONLINE,
                lessonStatus = LessonStatus.PENDING,
                studentNotes = null,
                amount = amount,
            )

            lessonRepository.createLesson(userId, request).fold(
                onSuccess = { lesson ->
                    _state.update { it.copy(isSubmitting = false) }
                    onSuccess(
                        "${tutor.firstName} ${tutor.lastName}",
                        lesson.subjectName,
                        lesson.lessonDate,
                        lesson.timeFrom.take(5),
                        lesson.timeTo.take(5),
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
        return (0..6).map { offset ->
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