package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.model.UserUpdateRequest
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.UserRepository

data class StudentProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val createdAt: String = "",
    val lessonsCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val fullName get() = "$firstName $lastName"
    val initials get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
}

data class ProfileEditState(
    val firstName: String = "",
    val lastName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _profile = MutableStateFlow(StudentProfile())
    val profile: StateFlow<StudentProfile> = _profile.asStateFlow()

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true) }

            val userId = tokenManager.userIdFlow.first() ?: run {
                _profile.update { it.copy(isLoading = false) }
                return@launch
            }
            tokenManager.roleFlow.first()

            userRepository.getUserById(userId).fold(
                onSuccess = { user ->
                    _profile.update {
                        it.copy(
                            firstName = user.firstName,
                            lastName = user.lastName,
                            email = user.email,
                            phoneNumber = user.phoneNumber,
                            role = user.role ?: UserRole.STUDENT,
                            createdAt = user.createdAt,
                        )
                    }
                },
                onFailure = { e ->
                    _profile.update { it.copy(isLoading = false, error = e.message) }
                    return@launch
                },
            )

            lessonRepository.getStudentLessons(userId).fold(
                onSuccess = { lessons -> _profile.update { it.copy(lessonsCount = lessons.size) } },
                onFailure = {},
            )

            _profile.update { it.copy(isLoading = false) }
        }
    }

    fun startEditing() {
        viewModelScope.launch {
            val p = _profile.first { !it.isLoading }
            _editState.value = ProfileEditState(firstName = p.firstName, lastName = p.lastName)
        }
    }

    fun onFirstNameChange(value: String) {
        if (value.length <= 50) _editState.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        if (value.length <= 50) _editState.update { it.copy(lastName = value) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val userId = tokenManager.userIdFlow.first() ?: return@launch
            _editState.update { it.copy(isLoading = true, error = null) }

            val request = UserUpdateRequest(
                firstName = _editState.value.firstName.trim(),
                lastName = _editState.value.lastName.trim(),
                avatarUrl = null,
            )

            userRepository.updateUser(userId, request).fold(
                onSuccess = { user ->
                    _profile.update {
                        it.copy(
                            firstName = user.firstName,
                            lastName = user.lastName
                        )
                    }
                    _editState.update { it.copy(isLoading = false, isSaved = true) }
                },
                onFailure = { e ->
                    _editState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }
}
