package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.AuthResponse
import pl.edu.ur.teachly.data.model.LoginRequest
import pl.edu.ur.teachly.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
}