package pl.edu.ur.teachly.data.remote

import okhttp3.MultipartBody
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.model.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(
        @Query("q") query: String? = null,
        @Query("role") role: UserRole? = null,
        @Query("active") active: Boolean? = null
    ): Response<List<UserResponse>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserResponse>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UserUpdateRequest): Response<UserResponse>

    @PUT("api/users/{id}/admin")
    suspend fun adminUpdateUser(@Path("id") id: Int, @Body request: AdminUserUpdateRequest): Response<UserResponse>

    @PATCH("api/users/{id}/activate")
    suspend fun activateUser(@Path("id") id: Int): Response<Unit>

    @DELETE("api/users/{id}")
    suspend fun deactivateUser(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/users/{id}/avatar")
    suspend fun uploadAvatar(@Path("id") id: Int, @Part file: MultipartBody.Part): Response<UserResponse>

    @DELETE("api/users/{id}/avatar")
    suspend fun deleteAvatar(@Path("id") id: Int): Response<UserResponse>
}
