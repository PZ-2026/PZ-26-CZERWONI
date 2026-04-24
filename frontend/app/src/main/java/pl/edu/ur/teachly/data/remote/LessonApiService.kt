package pl.edu.ur.teachly.data.remote

import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonRequest
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatusRequest
import pl.edu.ur.teachly.data.model.PaymentStatusRequest
import pl.edu.ur.teachly.data.model.StudentNotesRequest
import pl.edu.ur.teachly.data.model.TutorNotesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LessonApiService {

    @GET("api/lessons")
    suspend fun getAllLessons(): Response<List<LessonResponse>>

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

    @GET("api/lessons/{lessonId}")
    suspend fun getLesson(
        @Path("lessonId") lessonId: Int
    ): Response<LessonResponse>

    @PUT("api/lessons/{lessonId}/admin")
    suspend fun adminUpdateLesson(
        @Path("lessonId") lessonId: Int,
        @Body request: AdminLessonUpdateRequest
    ): Response<LessonResponse>

    @PATCH("api/lessons/{lessonId}/status")
    suspend fun changeLessonStatus(
        @Path("lessonId") lessonId: Int,
        @Body request: LessonStatusRequest
    ): Response<LessonResponse>

    @PATCH("api/lessons/{lessonId}/student-notes")
    suspend fun updateStudentNotes(
        @Path("lessonId") lessonId: Int,
        @Body request: StudentNotesRequest
    ): Response<LessonResponse>

    @PATCH("api/lessons/{lessonId}/tutor-notes")
    suspend fun updateTutorNotes(
        @Path("lessonId") lessonId: Int,
        @Body request: TutorNotesRequest
    ): Response<LessonResponse>

    @PATCH("api/lessons/{lessonId}/payment")
    suspend fun updatePaymentStatus(
        @Path("lessonId") lessonId: Int,
        @Body request: PaymentStatusRequest
    ): Response<LessonResponse>
}