package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminStatsResponse
import pl.edu.ur.teachly.data.remote.AdminApiService

class AdminRepository(private val api: AdminApiService) {

    suspend fun getStats(): Result<AdminStatsResponse> {
        return try {
            val response = api.getStats()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania statystyk"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}