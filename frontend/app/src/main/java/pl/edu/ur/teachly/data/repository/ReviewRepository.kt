package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.remote.ReviewApiService

class ReviewRepository(private val api: ReviewApiService) {

    suspend fun getTutorReviews(tutorId: Int): Result<List<ReviewResponse>> {
        return try {
            val response = api.getTutorReviews(tutorId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania recenzji"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun addReview(studentId: Int, request: ReviewRequest): Result<ReviewResponse> {
        return try {
            val response = api.addReview(studentId, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd dodania recenzji"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun getStudentReviews(studentId: Int): Result<List<ReviewResponse>> {
        return try {
            val response = api.getStudentReviews(studentId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania recenzji"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun updateReview(reviewId: Int, request: ReviewRequest): Result<ReviewResponse> {
        return try {
            val response = api.updateReview(reviewId, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji recenzji"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deleteReview(reviewId: Int): Result<Unit> {
        return try {
            val response = api.deleteReview(reviewId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usunięcia recenzji"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}