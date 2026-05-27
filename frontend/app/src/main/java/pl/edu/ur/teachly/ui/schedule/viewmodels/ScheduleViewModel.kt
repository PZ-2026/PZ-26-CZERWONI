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
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.ui.models.ScheduledClass
import pl.edu.ur.teachly.ui.models.toScheduledClass

data class ScheduleUiState(
    val confirmedClasses: List<ScheduledClass> = emptyList(),
    val pendingClasses: List<ScheduledClass> = emptyList(),
    val completedClasses: List<ScheduledClass> = emptyList(),
    val cancelledClasses: List<ScheduledClass> = emptyList(),
    val confirmedExpanded: Boolean = true,
    val pendingExpanded: Boolean = true,
    val completedExpanded: Boolean = false,
    val cancelledExpanded: Boolean = false,
    val userRole: UserRole = UserRole.STUDENT,
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
            val roleName = tokenManager.roleFlow.first() ?: "STUDENT"
            val role = try {
                UserRole.valueOf(roleName)
            } catch (e: Exception) {
                UserRole.STUDENT
            }

            val result = if (role == UserRole.TUTOR)
                lessonRepository.getTutorLessons(userId)
            else
                lessonRepository.getStudentLessons(userId)

            result.fold(
                onSuccess = { lessons ->
                    try {
                        val scheduled = lessons.map { it.toScheduledClass() }
                        _state.update {
                            it.copy(
                                confirmedClasses = scheduled.filter { c -> c.status == LessonStatus.CONFIRMED },
                                pendingClasses = scheduled.filter { c -> c.status == LessonStatus.PENDING },
                                completedClasses = scheduled.filter { c -> c.status == LessonStatus.COMPLETED },
                                cancelledClasses = scheduled.filter { c -> c.status == LessonStatus.CANCELLED },
                                userRole = role,
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

    fun toggleConfirmed() = _state.update { it.copy(confirmedExpanded = !it.confirmedExpanded) }
    fun togglePending() = _state.update { it.copy(pendingExpanded = !it.pendingExpanded) }
    fun toggleCompleted() = _state.update { it.copy(completedExpanded = !it.completedExpanded) }
    fun toggleCancelled() = _state.update { it.copy(cancelledExpanded = !it.cancelledExpanded) }
}
