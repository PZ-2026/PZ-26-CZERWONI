package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.repository.LessonRepository
import java.time.LocalDate

data class AdminLessonsState(
    val lessons: List<LessonResponse> = emptyList(),
    val filteredLessons: List<LessonResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: LessonStatus? = null,
    val selectedPaymentStatus: PaymentStatus? = null,
    val selectedFormat: LessonFormat? = null,
    val showOnlyUpcoming: Boolean? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminLessonsViewModel(
    private val lessonRepository: LessonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminLessonsState())
    val state: StateFlow<AdminLessonsState> = _state.asStateFlow()

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            lessonRepository.getAllLessons().fold(
                onSuccess = { lessons ->
                    _state.update { it.copy(lessons = lessons, isLoading = false) }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onStatusFilterChange(status: LessonStatus?) {
        _state.update { it.copy(selectedStatus = status) }
        applyFilters()
    }

    fun onPaymentStatusFilterChange(status: PaymentStatus?) {
        _state.update { it.copy(selectedPaymentStatus = status) }
        applyFilters()
    }

    fun onFormatFilterChange(format: LessonFormat?) {
        _state.update { it.copy(selectedFormat = format) }
        applyFilters()
    }

    fun onUpcomingFilterChange(upcoming: Boolean?) {
        _state.update { it.copy(showOnlyUpcoming = upcoming) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery.lowercase()
        val status = _state.value.selectedStatus
        val paymentStatus = _state.value.selectedPaymentStatus
        val format = _state.value.selectedFormat
        val upcoming = _state.value.showOnlyUpcoming
        val today = LocalDate.now()
        val filtered = _state.value.lessons.filter { lesson ->
            val matchesSearch = query.isEmpty() ||
                    lesson.tutorFirstName.lowercase().contains(query) ||
                    lesson.tutorLastName.lowercase().contains(query) ||
                    lesson.studentFirstName.lowercase().contains(query) ||
                    lesson.studentLastName.lowercase().contains(query) ||
                    lesson.subjectName.lowercase().contains(query)
            val matchesStatus = status == null || lesson.lessonStatus == status
            val matchesPayment = paymentStatus == null || lesson.paymentStatus == paymentStatus
            val matchesFormat = format == null || lesson.format == format
            val matchesUpcoming = upcoming == null || run {
                val lessonDate = runCatching { LocalDate.parse(lesson.lessonDate) }.getOrNull()
                if (upcoming) lessonDate != null && !lessonDate.isBefore(today)
                else lessonDate != null && lessonDate.isBefore(today)
            }
            matchesSearch && matchesStatus && matchesPayment && matchesFormat && matchesUpcoming
        }
        _state.update { it.copy(filteredLessons = filtered) }
    }

    fun updateLesson(lessonId: Int, request: AdminLessonUpdateRequest) {
        viewModelScope.launch {
            lessonRepository.adminUpdateLesson(lessonId, request).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            lessons = s.lessons.map { if (it.id == lessonId) updated else it },
                            successMessage = "Lekcja została zaktualizowana"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
