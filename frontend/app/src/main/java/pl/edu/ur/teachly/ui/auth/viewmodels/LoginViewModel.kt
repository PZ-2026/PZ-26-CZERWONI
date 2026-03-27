package pl.edu.ur.teachly.ui.auth.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.R

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val isSuccess: Boolean = false,
)

class LoginViewModel : ViewModel() {
    private val users = mapOf(
        "test@test.com" to "1234",
        "jan@example.com" to "jan",
        "user@user.com" to "user"
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val s = _uiState.value
        when {
            s.email.isBlank() || s.password.isBlank() ->
                _uiState.value = s.copy(errorMessage = R.string.error_empty_fields)

            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() ->
                _uiState.value = s.copy(errorMessage = R.string.error_invalid_email)

            else -> viewModelScope.launch {
                _uiState.value = s.copy(isLoading = true, errorMessage = null)
                delay(1200) // TODO: implement AuthRepository.login()
                _uiState.value = if (users[s.email] == s.password) {
                    _uiState.value.copy(isLoading = false, isSuccess = true)
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = R.string.error_invalid_credentials
                    )
                }
            }
        }
    }
}
