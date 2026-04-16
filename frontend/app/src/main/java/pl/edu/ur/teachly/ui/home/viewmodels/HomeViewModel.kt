package pl.edu.ur.teachly.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.UserRepository
import pl.edu.ur.teachly.ui.components.ScheduledClass
import pl.edu.ur.teachly.ui.components.toScheduledClass
import java.time.LocalDate

data class HomeUiState(
    val userName: String = "",
    val isStudent: Boolean = true,
    val isAdmin: Boolean = false,
    val upcomingLessons: List<ScheduledClass> = emptyList(),
    val totalLessons: Int = 0,
    val pendingLessons: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val lessonRepository: LessonRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = HomeUiState(isLoading = true)

            val userId = tokenManager.userIdFlow.first() ?: run {
                _state.value = HomeUiState(isLoading = false)
                return@launch
            }
            val role = tokenManager.roleFlow.first()
            val isStudent = role == "STUDENT"

            userRepository.getUserById(userId).fold(
                onSuccess = { user -> _state.value = _state.value.copy(userName = user.firstName) },
                onFailure = {},
            )

            val lessonsResult = when (role) {
                "STUDENT" -> lessonRepository.getStudentLessons(userId)
                "TUTOR" -> lessonRepository.getTutorLessons(userId)
                else -> {
                    _state.value = _state.value.copy(isStudent = isStudent, isLoading = false)
                    return@launch
                }
            }

            lessonsResult.fold(
                onSuccess = { lessons ->
                    try {
                        val today = LocalDate.now()
                        val upcoming = lessons
                            .filter { it.lessonStatus != LessonStatus.CANCELLED && it.lessonStatus != LessonStatus.COMPLETED }
                            .map { it.toScheduledClass() }
                            .filter { it.day >= today }
                            .sortedWith(compareBy({ it.day }, { it.time }))
                            .take(3)

                        _state.value = _state.value.copy(
                            isStudent = isStudent,
                            upcomingLessons = upcoming,
                            totalLessons = lessons.count { it.lessonStatus == LessonStatus.COMPLETED },
                            pendingLessons = lessons.count { it.lessonStatus == LessonStatus.PENDING },
                            isLoading = false,
                            error = null,
                        )
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            isStudent = isStudent,
                            isLoading = false,
                            error = e.message
                        )
                    }
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isStudent = isStudent,
                        isLoading = false,
                        error = e.message
                    )
                },
            )
        }
    }
}
