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
import pl.edu.ur.teachly.ui.util.Debouncer

data class AdminLessonsState(
    val lessons: List<LessonResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: LessonStatus? = null,
    val selectedPaymentStatus: PaymentStatus? = null,
    val selectedFormat: LessonFormat? = null,
    val showOnlyUpcoming: Boolean? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminLessonsViewModel(private val lessonRepository: LessonRepository) : ViewModel() {

    private val _state = MutableStateFlow(AdminLessonsState())
    val state: StateFlow<AdminLessonsState> = _state.asStateFlow()
    private val searchDebouncer = Debouncer(viewModelScope)

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val query = current.searchQuery.trim().takeIf { it.isNotBlank() }
            lessonRepository.getAllLessons(
                query = query,
                status = current.selectedStatus,
                paymentStatus = current.selectedPaymentStatus,
                format = current.selectedFormat,
                upcoming = current.showOnlyUpcoming
            ).fold(
                onSuccess = { lessons ->
                    _state.update { it.copy(lessons = lessons, isLoading = false) }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchDebouncer.submit { loadLessons() }
    }

    fun onStatusFilterChange(status: LessonStatus?) {
        _state.update { it.copy(selectedStatus = status) }
        searchDebouncer.cancel()
        loadLessons()
    }

    fun onPaymentStatusFilterChange(status: PaymentStatus?) {
        _state.update { it.copy(selectedPaymentStatus = status) }
        searchDebouncer.cancel()
        loadLessons()
    }

    fun onFormatFilterChange(format: LessonFormat?) {
        _state.update { it.copy(selectedFormat = format) }
        searchDebouncer.cancel()
        loadLessons()
    }

    fun onUpcomingFilterChange(upcoming: Boolean?) {
        _state.update { it.copy(showOnlyUpcoming = upcoming) }
        searchDebouncer.cancel()
        loadLessons()
    }

    fun updateLesson(lessonId: Int, request: AdminLessonUpdateRequest) {
        viewModelScope.launch {
            lessonRepository.adminUpdateLesson(lessonId, request).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Lekcja została zaktualizowana") }
                    loadLessons()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
