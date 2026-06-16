package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.AuthResponse
import pl.edu.ur.teachly.data.model.LoginRequest
import pl.edu.ur.teachly.data.model.RegisterRequest
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.remote.AuthApiService
import pl.edu.ur.teachly.data.remote.apiCall

class AuthRepository(private val api: AuthApiService, private val tokenManager: TokenManager) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        val result = apiCall("Błąd logowania") { api.login(LoginRequest(email, password)) }
        result.getOrNull()?.let { data ->
            tokenManager.saveAuthData(
                token = data.token,
                role = data.role,
                userId = data.userId
            )
        }
        return result
    }

    suspend fun register(
        userRole: UserRole,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): Result<AuthResponse> {
        val result = apiCall("Błąd rejestracji") {
            api.register(
                RegisterRequest(userRole, firstName, lastName, email, phoneNumber, password)
            )
        }
        result.getOrNull()?.let { data ->
            tokenManager.saveAuthData(
                token = data.token,
                role = data.role,
                userId = data.userId
            )
        }
        return result
    }

    suspend fun logout() {
        tokenManager.clearAuthData()
    }
}
