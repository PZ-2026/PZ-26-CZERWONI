package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserUpdateRequest
import pl.edu.ur.teachly.data.remote.UserApiService

class UserRepository(private val api: UserApiService) {

    suspend fun getUserById(id: Int): Result<UserResponse> {
        return try {
            val response = api.getUserById(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Nie znaleziono użytkownika"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun updateUser(id: Int, request: UserUpdateRequest): Result<UserResponse> {
        return try {
            val response = api.updateUser(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji profilu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}
