package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(): Response<List<UserResponse>>

    @GET("api/users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): Response<UserResponse>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UserUpdateRequest
    ): Response<UserResponse>

    @PUT("api/users/{id}/admin")
    suspend fun adminUpdateUser(
        @Path("id") id: Int,
        @Body request: AdminUserUpdateRequest
    ): Response<UserResponse>

    @PATCH("api/users/{id}/activate")
    suspend fun activateUser(
        @Path("id") id: Int
    ): Response<Unit>

    @DELETE("api/users/{id}")
    suspend fun deactivateUser(
        @Path("id") id: Int
    ): Response<Unit>
}