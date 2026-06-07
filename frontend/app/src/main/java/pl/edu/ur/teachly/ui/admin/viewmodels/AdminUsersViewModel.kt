package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import pl.edu.ur.teachly.data.local.TokenManager
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.UserRepository
import pl.edu.ur.teachly.ui.util.Debouncer

data class AdminUsersState(
    val users: List<UserResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedRole: UserRole? = null,
    val activeFilter: Boolean? = null,
    val currentUserId: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminUsersViewModel(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUsersState())
    val state: StateFlow<AdminUsersState> = _state.asStateFlow()
    private val searchDebouncer = Debouncer(viewModelScope)

    init {
        viewModelScope.launch {
            _state.update { it.copy(currentUserId = tokenManager.userIdFlow.first()) }
        }
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val query = current.searchQuery.trim().takeIf { it.isNotBlank() }
            userRepository.getAllUsers(query, current.selectedRole, current.activeFilter).fold(
                onSuccess = { users ->
                    _state.update { it.copy(users = users, isLoading = false) }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchDebouncer.submit { loadUsers() }
    }

    fun onRoleFilterChange(role: UserRole?) {
        _state.update { it.copy(selectedRole = role) }
        searchDebouncer.cancel()
        loadUsers()
    }

    fun onActiveFilterChange(activeFilter: Boolean?) {
        _state.update { it.copy(activeFilter = activeFilter) }
        searchDebouncer.cancel()
        loadUsers()
    }

    fun banUser(userId: Int) {
        if (userId == _state.value.currentUserId) {
            _state.update { it.copy(error = "Nie możesz zablokować własnego konta") }
            return
        }
        viewModelScope.launch {
            userRepository.deactivateUser(userId).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Konto użytkownika zostało zablokowane") }
                    loadUsers()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun unbanUser(userId: Int) {
        viewModelScope.launch {
            userRepository.activateUser(userId).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Konto użytkownika zostało odblokowane") }
                    loadUsers()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateUser(
        userId: Int,
        request: AdminUserUpdateRequest,
        pendingAvatarFile: java.io.File?,
        pendingDeleteAvatar: Boolean
    ) {
        if (userId == _state.value.currentUserId) {
            _state.update { it.copy(error = "Nie możesz edytować własnego konta z panelu administratora") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            if (pendingDeleteAvatar) {
                userRepository.deleteAvatar(userId).fold(
                    onSuccess = { },
                    onFailure = { e ->
                        _state.update {
                            it.copy(isLoading = false, error = "Błąd podczas usuwania zdjęcia: ${e.message}")
                        }
                        return@launch
                    }
                )
            } else if (pendingAvatarFile != null) {
                val file = pendingAvatarFile
                val mimeType = when (file.extension.lowercase()) {
                    "png" -> "image/png"
                    "gif" -> "image/gif"
                    "jpg", "jpeg" -> "image/jpeg"
                    else -> "image/jpeg"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                userRepository.uploadAvatar(userId, body).fold(
                    onSuccess = { },
                    onFailure = { e ->
                        _state.update {
                            it.copy(isLoading = false, error = "Błąd podczas zapisywania zdjęcia: ${e.message}")
                        }
                        return@launch
                    }
                )
            }

            userRepository.adminUpdateUser(userId, request).fold(
                onSuccess = {
                    _state.update {
                        it.copy(isLoading = false, successMessage = "Dane użytkownika zostały zaktualizowane")
                    }
                    loadUsers()
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
