package pl.edu.ur.teachly.ui.auth.viewmodels

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.repository.AuthRepository

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val isSuccess: Boolean = false,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val repository = AuthRepository(tokenManager)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value
        when {
            state.email.isBlank() || state.password.isBlank() ->
                _uiState.value = state.copy(errorMessage = R.string.error_empty_fields)

            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() ->
                _uiState.value = state.copy(errorMessage = R.string.error_invalid_email)

            else -> viewModelScope.launch {
                _uiState.value = state.copy(isLoading = true, errorMessage = null)

                val result = repository.login(state.email, state.password)

                _uiState.value = result.fold(
                    onSuccess = {
                        state.copy(isLoading = false, isSuccess = true)
                    },
                    onFailure = {
                        state.copy(
                            isLoading = false,
                            errorMessage = R.string.error_invalid_credentials
                        )
                    }
                )
            }
        }
    }
}
