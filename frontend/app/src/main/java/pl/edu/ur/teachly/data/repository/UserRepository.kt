package pl.edu.ur.teachly.data.repository

import okhttp3.MultipartBody
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.model.UserUpdateRequest
import pl.edu.ur.teachly.data.remote.UserApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class UserRepository(private val api: UserApiService) {

    suspend fun getUserById(id: Int): Result<UserResponse> =
        apiCall("Nie znaleziono użytkownika") { api.getUserById(id) }

    suspend fun getAllUsers(
        query: String? = null,
        role: UserRole? = null,
        active: Boolean? = null
    ): Result<List<UserResponse>> =
        apiCall("Błąd pobierania użytkowników") { api.getAllUsers(query, role, active) }

    suspend fun updateUser(id: Int, request: UserUpdateRequest): Result<UserResponse> =
        apiCall("Błąd aktualizacji profilu") { api.updateUser(id, request) }

    suspend fun adminUpdateUser(id: Int, request: AdminUserUpdateRequest): Result<UserResponse> =
        apiCall("Błąd aktualizacji użytkownika") { api.adminUpdateUser(id, request) }

    suspend fun activateUser(id: Int): Result<Unit> =
        apiCallUnit("Błąd aktywacji użytkownika") { api.activateUser(id) }

    suspend fun deactivateUser(id: Int): Result<Unit> =
        apiCallUnit("Błąd deaktywacji użytkownika") { api.deactivateUser(id) }

    suspend fun uploadAvatar(id: Int, file: MultipartBody.Part): Result<UserResponse> =
        apiCall("Błąd wysyłania zdjęcia") { api.uploadAvatar(id, file) }

    suspend fun deleteAvatar(id: Int): Result<UserResponse> =
        apiCall("Błąd usuwania zdjęcia") { api.deleteAvatar(id) }
}
