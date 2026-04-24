package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.AdminStatsResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdminApiService {

    @GET("api/admin/stats")
    suspend fun getStats(): Response<AdminStatsResponse>
}
