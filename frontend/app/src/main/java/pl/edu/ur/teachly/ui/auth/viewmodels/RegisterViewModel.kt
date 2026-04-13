package pl.edu.ur.teachly.ui.auth.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.repository.AuthRepository

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
    val selectedRole: UserRole? = UserRole.STUDENT,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

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
        _uiState.value = _uiState.value.copy(firstName = v, errorMessage = null)
    }

    fun onLastNameChange(v: String) {
        _uiState.value = _uiState.value.copy(lastName = v, errorMessage = null)
    }

    fun onEmailChange(v: String) {
        _uiState.value = _uiState.value.copy(email = v, errorMessage = null)
    }

    fun onPhoneChange(v: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = v, errorMessage = null)
    }

    fun onPasswordChange(v: String) {
        _uiState.value = _uiState.value.copy(password = v, errorMessage = null)
    }

    fun register() {
        val state = _uiState.value
        when {
            state.firstName.isBlank() -> _uiState.value = state.copy(errorMessage = "Podaj imię")
            state.lastName.isBlank() -> _uiState.value =
                state.copy(errorMessage = "Podaj nazwisko")

            state.email.isBlank() -> _uiState.value =
                state.copy(errorMessage = "Podaj adres e-mail")

            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches()
                -> _uiState.value = state.copy(errorMessage = "Podaj poprawny adres e-mail")

            state.phoneNumber.isBlank() -> _uiState.value =
                state.copy(errorMessage = "Podaj numer telefonu")

            !Patterns.PHONE.matcher(state.phoneNumber).matches()
                -> _uiState.value = state.copy(errorMessage = "Podaj poprawny numer telefonu")

            state.password.length < 8 -> _uiState.value =
                state.copy(errorMessage = "Hasło musi mieć min. 8 znaków")

            else -> viewModelScope.launch {
                _uiState.value = state.copy(isLoading = true, errorMessage = null)


                // TODO: Add phoneNumber text field to RegisterScreen
                val selectedRole = state.selectedRole ?: UserRole.STUDENT
                val result = repository.register(
                    selectedRole,
                    state.firstName,
                    state.lastName,
                    state.email,
                    state.phoneNumber,
                    state.password
                )

                _uiState.value = result.fold(
                    onSuccess = {
                        state.copy(isLoading = false, isSuccess = true)
                    },
                    onFailure = { exception ->
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Błąd przy rejestracji"
                        )
                    }
                )
            }
        }
    }
}
