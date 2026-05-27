package pl.edu.ur.teachly.data.repository

import org.json.JSONObject
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.AuthResponse
import pl.edu.ur.teachly.data.model.LoginRequest
import pl.edu.ur.teachly.data.model.RegisterRequest
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.remote.AuthApiService
import retrofit2.Response

class AuthRepository(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {

    private fun parseErrorDetail(response: Response<*>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val jsonObject = JSONObject(errorBody)
                if (jsonObject.has("detail")) {
                    return jsonObject.getString("detail")
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

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
                val detail = parseErrorDetail(response) ?: "Błąd logowania"
                Result.failure(Exception(detail))
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
                val detail = parseErrorDetail(response) ?: "Błąd rejestracji"
                Result.failure(Exception(detail))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun logout() {
        tokenManager.clearAuthData()
    }
}