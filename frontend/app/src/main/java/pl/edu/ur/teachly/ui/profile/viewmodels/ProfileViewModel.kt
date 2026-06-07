package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
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
    val avatarUrl: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val fullName get() = "$firstName $lastName"
    val initials get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
}

data class ProfileEditState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val requiresRelogin: Boolean = false,
    val pendingAvatarFile: File? = null,
    val pendingDeleteAvatar: Boolean = false,
    val localAvatarUrl: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
    private val reportRepository: pl.edu.ur.teachly.data.repository.ReportRepository
) : ViewModel() {

    private val _profile = MutableStateFlow(StudentProfile())
    val profile: StateFlow<StudentProfile> = _profile.asStateFlow()

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    private var hasLoaded = false

    fun loadProfile() {
        viewModelScope.launch {
            if (!hasLoaded) {
                _profile.update { it.copy(isLoading = true, error = null) }
            }

            val userId = tokenManager.userIdFlow.first() ?: run {
                _profile.update { it.copy(isLoading = false) }
                return@launch
            }
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
                            avatarUrl = user.avatarUrl?.takeIf {
                                it.isNotBlank() &&
                                    !it.equals("null", ignoreCase = true)
                            }
                        )
                    }
                },
                onFailure = { e ->
                    _profile.update { it.copy(isLoading = false, error = e.message) }
                    return@launch
                }
            )

            lessonRepository.getStudentLessons(userId).fold(
                onSuccess = { lessons ->
                    val completedCount =
                        lessons.count { it.lessonStatus == LessonStatus.COMPLETED }
                    _profile.update { it.copy(lessonsCount = completedCount) }
                },
                onFailure = {}
            )

            hasLoaded = true
            _profile.update { it.copy(isLoading = false) }
        }
    }

    fun startEditing() {
        viewModelScope.launch {
            val p = _profile.first { !it.isLoading }
            _editState.value = ProfileEditState(
                firstName = p.firstName,
                lastName = p.lastName,
                email = p.email,
                phoneNumber = p.phoneNumber?.filter { it.isDigit() } ?: ""
            )
        }
    }

    fun onFirstNameChange(value: String) {
        if (value.length <= 50) _editState.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        if (value.length <= 50) _editState.update { it.copy(lastName = value) }
    }

    fun onEmailChange(value: String) {
        _editState.update { it.copy(email = value) }
    }

    fun onPhoneNumberChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(9)
        _editState.update { it.copy(phoneNumber = digitsOnly) }
    }

    fun onPasswordChange(value: String) {
        _editState.update { it.copy(password = value) }
    }

    fun resetEditState() {
        _editState.update { it.copy(isSaved = false, requiresRelogin = false) }
    }

    fun setPendingAvatar(file: File) {
        _editState.update {
            it.copy(
                pendingAvatarFile = file,
                pendingDeleteAvatar = false,
                localAvatarUrl = file.absolutePath
            )
        }
    }

    fun setPendingDeleteAvatar() {
        _editState.update {
            it.copy(
                pendingAvatarFile = null,
                pendingDeleteAvatar = true,
                localAvatarUrl = null
            )
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val userId = tokenManager.userIdFlow.first() ?: return@launch
            _editState.update { it.copy(isLoading = true, error = null) }

            val state = _editState.value

            if (state.firstName.trim().isBlank()) {
                _editState.update { it.copy(isLoading = false, error = "Imię nie może być puste") }
                return@launch
            }
            if (state.lastName.trim().isBlank()) {
                _editState.update { it.copy(isLoading = false, error = "Nazwisko nie może być puste") }
                return@launch
            }
            if (state.email.trim().isBlank() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()
            ) {
                _editState.update { it.copy(isLoading = false, error = "Niepoprawny format adresu email") }
                return@launch
            }
            val digitsPhone = state.phoneNumber.filter { it.isDigit() }
            if (digitsPhone.length != 9) {
                _editState.update { it.copy(isLoading = false, error = "Numer telefonu musi składać się z 9 cyfr") }
                return@launch
            }
            if (state.password.isNotBlank() && state.password.length < 8) {
                _editState.update {
                    it.copy(isLoading = false, error = "Hasło musi mieć co najmniej 8 znaków")
                }
                return@launch
            }

            var updatedAvatarUrl: String? = profile.value.avatarUrl
            if (state.pendingDeleteAvatar) {
                userRepository.deleteAvatar(userId).fold(
                    onSuccess = { user -> updatedAvatarUrl = null },
                    onFailure = { e ->
                        _editState.update { it.copy(isLoading = false, error = "Błąd usuwania zdjęcia: ${e.message}") }
                        return@launch
                    }
                )
            } else if (state.pendingAvatarFile != null) {
                val file = state.pendingAvatarFile
                val mimeType = when (file.extension.lowercase()) {
                    "png" -> "image/png"
                    "gif" -> "image/gif"
                    "jpg", "jpeg" -> "image/jpeg"
                    else -> "image/jpeg"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                userRepository.uploadAvatar(userId, body).fold(
                    onSuccess = { user -> updatedAvatarUrl = user.avatarUrl },
                    onFailure = { e ->
                        _editState.update {
                            it.copy(isLoading = false, error = "Błąd zapisywania zdjęcia: ${e.message}")
                        }
                        return@launch
                    }
                )
            }

            val request = UserUpdateRequest(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                email = state.email.trim(),
                phoneNumber = digitsPhone,
                password = state.password.takeIf { it.isNotBlank() },
                avatarUrl = updatedAvatarUrl
            )

            val requiresRelogin =
                state.email.trim() != profile.value.email || !state.password.isNullOrBlank()

            userRepository.updateUser(userId, request).fold(
                onSuccess = { user ->
                    if (requiresRelogin) {
                        tokenManager.clearAuthData()
                    } else {
                        _profile.update {
                            it.copy(
                                firstName = user.firstName,
                                lastName = user.lastName,
                                email = user.email,
                                phoneNumber = user.phoneNumber,
                                avatarUrl = user.avatarUrl?.takeIf { it != "null" }
                            )
                        }
                    }
                    _editState.update {
                        it.copy(
                            isLoading = false,
                            isSaved = true,
                            requiresRelogin = requiresRelogin,
                            pendingAvatarFile = null,
                            pendingDeleteAvatar = false,
                            localAvatarUrl = null
                        )
                    }
                },
                onFailure = { e ->
                    _editState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun downloadReport(
        startDate: String,
        endDate: String,
        type: String,
        includeFields: List<String>,
        onResult: (Result<java.io.File>) -> Unit
    ) {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true, error = null) }
            val result = reportRepository.downloadReport(startDate, endDate, type, includeFields)
            result.onFailure { e ->
                _profile.update { it.copy(isLoading = false, error = e.message) }
            }
            result.onSuccess {
                _profile.update { it.copy(isLoading = false) }
            }
            onResult(result)
        }
    }

    fun uploadAvatar(file: File, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true, error = null) }
            val userId = tokenManager.userIdFlow.first() ?: return@launch
            val mimeType = when (file.extension.lowercase()) {
                "png" -> "image/png"
                "gif" -> "image/gif"
                "jpg", "jpeg" -> "image/jpeg"
                else -> "image/jpeg"
            }
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            userRepository.uploadAvatar(userId, body).fold(
                onSuccess = { user ->
                    _profile.update {
                        it.copy(
                            avatarUrl = user.avatarUrl?.takeIf { url -> url != "null" },
                            isLoading = false
                        )
                    }
                    onResult(true)
                },
                onFailure = {
                    _profile.update { it.copy(isLoading = false) }
                    onResult(false)
                }
            )
        }
    }

    fun deleteAvatar(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _profile.update { it.copy(isLoading = true, error = null) }
            val userId = tokenManager.userIdFlow.first() ?: return@launch
            userRepository.deleteAvatar(userId).fold(
                onSuccess = { user ->
                    _profile.update {
                        it.copy(
                            avatarUrl = null,
                            isLoading = false
                        )
                    }
                    onResult(true)
                },
                onFailure = {
                    _profile.update { it.copy(isLoading = false) }
                    onResult(false)
                }
            )
        }
    }
}
