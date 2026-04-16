package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.LessonRequest
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface LessonApiService {

    @POST("api/lessons/student/{studentId}")
    suspend fun createLesson(
        @Path("studentId") studentId: Int,
        @Body request: LessonRequest
    ): Response<LessonResponse>

    @GET("api/lessons/student/{studentId}")
    suspend fun getStudentLessons(
        @Path("studentId") studentId: Int
    ): Response<List<LessonResponse>>

    @GET("api/lessons/tutor/{tutorId}")
    suspend fun getTutorLessons(
        @Path("tutorId") tutorId: Int
    ): Response<List<LessonResponse>>

    @PATCH("api/lessons/{lessonId}/status")
    suspend fun changeLessonStatus(
        @Path("lessonId") lessonId: Int,
        @Body request: LessonStatusRequest
    ): Response<LessonResponse>
}