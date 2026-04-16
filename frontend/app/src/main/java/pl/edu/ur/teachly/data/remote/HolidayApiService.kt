package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.HolidayRequest
import pl.edu.ur.teachly.data.model.HolidayResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HolidayApiService {

    @GET("api/holidays")
    suspend fun getAllHolidays(): Response<List<HolidayResponse>>

    @POST("api/holidays")
    suspend fun addHoliday(
        @Body request: HolidayRequest
    ): Response<HolidayResponse>

    @PUT("api/holidays/{id}")
    suspend fun updateHoliday(
        @Path("id") id: Int,
        @Body request: HolidayRequest
    ): Response<HolidayResponse>

    @DELETE("api/holidays/{id}")
    suspend fun deleteHoliday(
        @Path("id") id: Int
    ): Response<Unit>
}