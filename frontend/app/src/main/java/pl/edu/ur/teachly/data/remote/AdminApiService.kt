package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.AdminStatsResponse
import pl.edu.ur.teachly.data.model.ReviewResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface AdminApiService {

    @GET("api/admin/stats")
    suspend fun getStats(): Response<AdminStatsResponse>

    @GET("api/admin/reviews")
    suspend fun getAllReviews(): Response<List<ReviewResponse>>

    @DELETE("api/admin/reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: Int): Response<Unit>
}
