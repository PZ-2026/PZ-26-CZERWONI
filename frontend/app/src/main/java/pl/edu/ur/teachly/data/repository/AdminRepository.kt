package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminStatsResponse
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.remote.AdminApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class AdminRepository(private val api: AdminApiService) {

    suspend fun getStats(): Result<AdminStatsResponse> =
        apiCall("Błąd pobierania statystyk") { api.getStats() }

    suspend fun getAllReviews(
        query: String? = null,
        rating: Int? = null
    ): Result<List<ReviewResponse>> =
        apiCall("Błąd pobierania opinii") { api.getAllReviews(query, rating) }

    suspend fun deleteReview(reviewId: Int): Result<Unit> =
        apiCallUnit("Błąd usunięcia opinii") { api.deleteReview(reviewId) }
}
