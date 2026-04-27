package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.UserRepository

data class AdminUsersState(
    val users: List<UserResponse> = emptyList(),
    val filteredUsers: List<UserResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedRole: UserRole? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminUsersViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUsersState())
    val state: StateFlow<AdminUsersState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            userRepository.getAllUsers().fold(
                onSuccess = { users ->
                    _state.update { it.copy(users = users, isLoading = false) }
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

    fun onRoleFilterChange(role: UserRole?) {
        _state.update { it.copy(selectedRole = role) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery.lowercase()
        val role = _state.value.selectedRole
        val filtered = _state.value.users.filter { user ->
            val matchesSearch = query.isEmpty() ||
                    user.firstName.lowercase().contains(query) ||
                    user.lastName.lowercase().contains(query) ||
                    user.email.lowercase().contains(query)
            val matchesRole = role == null || user.role == role
            matchesSearch && matchesRole
        }
        _state.update { it.copy(filteredUsers = filtered) }
    }

    fun banUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deactivateUser(userId).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(
                            users = s.users.map { if (it.id == userId) it.copy(isActive = false) else it },
                            successMessage = "Konto użytkownika zostało zablokowane"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun unbanUser(userId: Int) {
        viewModelScope.launch {
            userRepository.activateUser(userId).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(
                            users = s.users.map { if (it.id == userId) it.copy(isActive = true) else it },
                            successMessage = "Konto użytkownika zostało odblokowane"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateUser(userId: Int, request: AdminUserUpdateRequest) {
        viewModelScope.launch {
            userRepository.adminUpdateUser(userId, request).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            users = s.users.map { if (it.id == userId) updated else it },
                            successMessage = "Dane użytkownika zostały zaktualizowane"
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
