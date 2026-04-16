package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.SubjectCategoryRequest
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.data.model.SubjectRequest
import pl.edu.ur.teachly.data.model.SubjectResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubjectApiService {

    @GET("api/subjects")
    suspend fun getAllSubjects(): Response<List<SubjectResponse>>

    @POST("api/subjects")
    suspend fun addSubject(
        @Body request: SubjectRequest
    ): Response<SubjectResponse>

    @DELETE("api/subjects/{id}")
    suspend fun deleteSubject(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("api/subjects/categories")
    suspend fun getAllCategories(): Response<List<SubjectCategoryResponse>>

    @POST("api/subjects/categories")
    suspend fun addCategory(
        @Body request: SubjectCategoryRequest
    ): Response<SubjectCategoryResponse>

    @DELETE("api/subjects/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<Unit>
}