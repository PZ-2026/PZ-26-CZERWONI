package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.remote.ReviewApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class ReviewRepository(private val api: ReviewApiService) {

    suspend fun getTutorReviews(tutorId: Int): Result<List<ReviewResponse>> =
        apiCall("Błąd pobierania recenzji") { api.getTutorReviews(tutorId) }

    suspend fun addReview(studentId: Int, request: ReviewRequest): Result<ReviewResponse> =
        apiCall("Błąd dodania recenzji") { api.addReview(studentId, request) }

    suspend fun getStudentReviews(studentId: Int): Result<List<ReviewResponse>> =
        apiCall("Błąd pobierania recenzji") { api.getStudentReviews(studentId) }

    suspend fun updateReview(reviewId: Int, request: ReviewRequest): Result<ReviewResponse> =
        apiCall("Błąd aktualizacji recenzji") { api.updateReview(reviewId, request) }

    suspend fun deleteReview(reviewId: Int): Result<Unit> =
        apiCallUnit("Błąd usunięcia recenzji") { api.deleteReview(reviewId) }
}
