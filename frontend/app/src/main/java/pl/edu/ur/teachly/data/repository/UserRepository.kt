package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
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

    suspend fun getAllUsers(): Result<List<UserResponse>> {
        return try {
            val response = api.getAllUsers()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania użytkowników"))
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

    suspend fun adminUpdateUser(id: Int, request: AdminUserUpdateRequest): Result<UserResponse> {
        return try {
            val response = api.adminUpdateUser(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji użytkownika"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun activateUser(id: Int): Result<Unit> {
        return try {
            val response = api.activateUser(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd aktywacji użytkownika"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deactivateUser(id: Int): Result<Unit> {
        return try {
            val response = api.deactivateUser(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd deaktywacji użytkownika"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}
