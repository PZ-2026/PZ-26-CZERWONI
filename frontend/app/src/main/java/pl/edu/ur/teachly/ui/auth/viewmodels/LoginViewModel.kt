package pl.edu.ur.teachly.ui.auth.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.repository.AuthRepository

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val errorText: String? = null,
    val isSuccess: Boolean = false,
)

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        if (value.length <= 100) {
            _uiState.value =
                _uiState.value.copy(email = value, errorMessage = null, errorText = null)
        }
    }

    fun onPasswordChange(value: String) {
        if (value.length <= 100) {
            _uiState.value =
                _uiState.value.copy(password = value, errorMessage = null, errorText = null)
        }
    }

    fun login() {
        val state = _uiState.value
        when {
            state.email.isBlank() || state.password.isBlank() ->
                _uiState.value = state.copy(errorMessage = R.string.error_empty_fields)

            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() ->
                _uiState.value = state.copy(errorMessage = R.string.error_invalid_email)

            else -> viewModelScope.launch {
                _uiState.value = state.copy(isLoading = true, errorMessage = null, errorText = null)

                val result = repository.login(state.email.trim().lowercase(), state.password)

                _uiState.value = result.fold(
                    onSuccess = {
                        state.copy(isLoading = false, isSuccess = true)
                    },
                    onFailure = { exception ->
                        state.copy(
                            isLoading = false,
                            errorText = exception.message
                        )
                    }
                )
            }
        }
    }
}
