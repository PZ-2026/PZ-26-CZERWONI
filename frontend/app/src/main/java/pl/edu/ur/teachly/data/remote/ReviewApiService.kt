package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.ReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewApiService {

    @GET("api/reviews/tutor/{tutorId}")
    suspend fun getTutorReviews(
        @Path("tutorId") tutorId: Int
    ): Response<List<ReviewResponse>>

    @POST("api/reviews/student/{studentId}")
    suspend fun addReview(
        @Path("studentId") studentId: Int,
        @Body request: ReviewRequest
    ): Response<ReviewResponse>

    @PUT("api/reviews/{id}")
    suspend fun updateReview(
        @Path("id") id: Int,
        @Body request: ReviewRequest
    ): Response<ReviewResponse>

    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(
        @Path("id") id: Int
    ): Response<Unit>
}