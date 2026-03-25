package pl.edu.ur.teachly.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class UserRole(
    val emoji: String,
    val title: String,
    val description: String,
) {
    STUDENT("🎓", "Jestem uczniem", "Szukam korepetytora i chcę umawiać lekcje"),
    TUTOR("📖", "Jestem korepetytorem", "Oferuję lekcje i zarządzam harmonogramem"),
}

data class RegisterUiState(
    val step: Int = 1,
    val selectedRole: UserRole? = null,
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onRoleSelected(role: UserRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onNextStep() {
        if (_uiState.value.selectedRole != null) _uiState.value = _uiState.value.copy(step = 2)
    }

    fun onPreviousStep() {
        _uiState.value = _uiState.value.copy(step = 1, errorMessage = null)
    }

    fun onFirstNameChange(v: String) {
        _uiState.value = _uiState.value.copy(first_name = v, errorMessage = null)
    }

    fun onLastNameChange(v: String) {
        _uiState.value = _uiState.value.copy(last_name = v, errorMessage = null)
    }

    fun onEmailChange(v: String) {
        _uiState.value = _uiState.value.copy(email = v, errorMessage = null)
    }

    fun onPasswordChange(v: String) {
        _uiState.value = _uiState.value.copy(password = v, errorMessage = null)
    }

    fun register() {
        val s = _uiState.value
        when {
            s.first_name.isBlank() -> _uiState.value = s.copy(errorMessage = "Podaj imię")
            s.last_name.isBlank() -> _uiState.value = s.copy(errorMessage = "Podaj nazwisko")
            s.email.isBlank() -> _uiState.value = s.copy(errorMessage = "Podaj adres e-mail")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()
                -> _uiState.value = s.copy(errorMessage = "Podaj poprawny adres e-mail")

            s.password.length < 8 -> _uiState.value =
                s.copy(errorMessage = "Hasło musi mieć min. 8 znaków")

            else -> viewModelScope.launch {
                _uiState.value = s.copy(isLoading = true, errorMessage = null)
                delay(1200) // TODO: implement AuthRepository.register()
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            }
        }
    }
}
