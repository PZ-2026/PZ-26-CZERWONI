package pl.edu.ur.teachly.ui.lesson.viewmodels

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
import pl.edu.ur.teachly.data.model.LessonStatusRequest
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.ui.models.LessonDetail
import pl.edu.ur.teachly.ui.models.toUiLessonDetail

data class LessonDetailUiState(
    val lesson: LessonDetail? = null,
    val currentUserId: Int? = null,
    val currentUserRole: UserRole? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false,
    val actionError: String? = null,
    val actionSuccess: String? = null,
)

class LessonDetailViewModel(
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LessonDetailUiState())
    val state: StateFlow<LessonDetailUiState> = _state.asStateFlow()

    fun load(lessonId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val userId = tokenManager.userIdFlow.first()
            val roleName = tokenManager.roleFlow.first()
            val role = try {
                UserRole.valueOf(roleName ?: "STUDENT")
            } catch (e: Exception) {
                UserRole.STUDENT
            }
            lessonRepository.getLesson(lessonId).fold(
                onSuccess = { lesson ->
                    _state.update {
                        it.copy(
                            lesson = lesson.toUiLessonDetail(),
                            currentUserId = userId,
                            currentUserRole = role,
                            isLoading = false,
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    fun changeStatus(lessonId: Int, newStatus: LessonStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            lessonRepository.changeLessonStatus(lessonId, LessonStatusRequest(newStatus, null))
                .fold(
                    onSuccess = { updated ->
                        _state.update {
                            it.copy(
                                lesson = updated.toUiLessonDetail(),
                                isSaving = false,
                                actionSuccess = "Status zmieniony"
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isSaving = false, actionError = e.message) }
                    },
                )
        }
    }

    fun saveStudentNotes(lessonId: Int, notes: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            lessonRepository.updateStudentNotes(lessonId, notes).fold(
                onSuccess = { updated ->
                    _state.update {
                        it.copy(
                            lesson = updated.toUiLessonDetail(),
                            isSaving = false,
                            actionSuccess = "Notatki zapisane"
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, actionError = e.message) }
                },
            )
        }
    }

    fun saveTutorNotes(lessonId: Int, notes: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            lessonRepository.updateTutorNotes(lessonId, notes).fold(
                onSuccess = { updated ->
                    _state.update {
                        it.copy(
                            lesson = updated.toUiLessonDetail(),
                            isSaving = false,
                            actionSuccess = "Notatki zapisane"
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, actionError = e.message) }
                },
            )
        }
    }

    fun markPaid(lessonId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            lessonRepository.updatePaymentStatus(lessonId, PaymentStatus.PAID).fold(
                onSuccess = { updated ->
                    _state.update {
                        it.copy(
                            lesson = updated.toUiLessonDetail(),
                            isSaving = false,
                            actionSuccess = "Oznaczono jako opłaconą"
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, actionError = e.message) }
                },
            )
        }
    }

    fun clearMessages() {
        _state.update { it.copy(actionError = null, actionSuccess = null) }
    }

    fun isThirtyMinutesAfterStart(lesson: LessonDetail): Boolean {
        return try {
            val start = lesson.lessonDate.atTime(lesson.timeFrom)
            java.time.LocalDateTime.now().isAfter(start.plusMinutes(30))
        } catch (e: Exception) {
            false
        }
    }
}
