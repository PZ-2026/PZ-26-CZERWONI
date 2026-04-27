package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminStatsResponse
import pl.edu.ur.teachly.data.model.ReviewResponse
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

    suspend fun getAllReviews(): Result<List<ReviewResponse>> {
        return try {
            val response = api.getAllReviews()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania opinii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deleteReview(reviewId: Int): Result<Unit> {
        return try {
            val response = api.deleteReview(reviewId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usunięcia opinii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}