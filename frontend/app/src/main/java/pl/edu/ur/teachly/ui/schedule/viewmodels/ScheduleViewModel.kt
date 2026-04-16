package pl.edu.ur.teachly.ui.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.ui.components.ScheduledClass
import pl.edu.ur.teachly.ui.components.toScheduledClass

data class ScheduleUiState(
    val upcomingClasses: List<ScheduledClass> = emptyList(),
    val finishedClasses: List<ScheduledClass> = emptyList(),
    val isStudent: Boolean = true,
    val expanded: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class ScheduleViewModel(
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleUiState())
    val state: StateFlow<ScheduleUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = tokenManager.userIdFlow.first() ?: run {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            val role = tokenManager.roleFlow.first()

            val result = if (role == "TUTOR") {
                lessonRepository.getTutorLessons(userId)
            } else {
                lessonRepository.getStudentLessons(userId)
            }

            result.fold(
                onSuccess = { lessons ->
                    try {
                        val scheduled = lessons.map { it.toScheduledClass() }
                        _state.update {
                            it.copy(
                                upcomingClasses = scheduled.filter { c ->
                                    c.status != "Zakończone" && c.status != "Anulowane"
                                },
                                finishedClasses = scheduled.filter { c ->
                                    c.status == "Zakończone"
                                },
                                isStudent = role != "TUTOR",
                                isLoading = false,
                            )
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    fun toggleExpanded() {
        _state.update { it.copy(expanded = !it.expanded) }
    }
}
