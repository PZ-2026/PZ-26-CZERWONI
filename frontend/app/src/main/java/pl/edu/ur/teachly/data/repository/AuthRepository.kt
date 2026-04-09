package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.AuthResponse
import pl.edu.ur.teachly.data.model.LoginRequest
import pl.edu.ur.teachly.data.model.RegisterRequest
import pl.edu.ur.teachly.data.remote.RetrofitClient
import pl.edu.ur.teachly.ui.auth.viewmodels.UserRole

class AuthRepository(private val tokenManager: TokenManager) {

    private val api = RetrofitClient.authApi

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val data = response.body()!!
                tokenManager.saveAuthData(
                    token = data.token,
                    role = data.role,
                    userId = data.userId
                )
                Result.success(data)
            } else {
                val message = "Błąd logowania"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun register(
        userRole: UserRole,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            val response = api.register(
                RegisterRequest(userRole, firstName, lastName, email, phoneNumber, password)
            )
            if (response.isSuccessful) {
                val data = response.body()!!
                tokenManager.saveAuthData(
                    token = data.token,
                    role = data.role,
                    userId = data.userId
                )
                Result.success(data)
            } else {
                val message = "Błąd rejestracji"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun logout() {
        tokenManager.clearAuthData()
    }
}