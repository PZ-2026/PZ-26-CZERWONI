package pl.edu.ur.teachly.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ReportApiService {
    @Streaming
    @GET("api/reports/my")
    suspend fun getMyReport(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<ResponseBody>
}
