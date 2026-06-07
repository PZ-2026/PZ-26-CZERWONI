package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonRequest
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.LessonStatusRequest
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.model.PaymentStatusRequest
import pl.edu.ur.teachly.data.model.StudentNotesRequest
import pl.edu.ur.teachly.data.model.TutorNotesRequest
import pl.edu.ur.teachly.data.remote.LessonApiService
import pl.edu.ur.teachly.data.remote.apiCall

class LessonRepository(private val api: LessonApiService) {

    suspend fun getAllLessons(
        query: String? = null,
        status: LessonStatus? = null,
        paymentStatus: PaymentStatus? = null,
        format: LessonFormat? = null,
        upcoming: Boolean? = null
    ): Result<List<LessonResponse>> =
        apiCall("Błąd pobierania lekcji") { api.getAllLessons(query, status, paymentStatus, format, upcoming) }

    suspend fun adminUpdateLesson(lessonId: Int, request: AdminLessonUpdateRequest): Result<LessonResponse> =
        apiCall("Błąd aktualizacji lekcji") { api.adminUpdateLesson(lessonId, request) }

    suspend fun createLesson(studentId: Int, request: LessonRequest): Result<LessonResponse> =
        apiCall("Błąd tworzenia lekcji") { api.createLesson(studentId, request) }

    suspend fun getStudentLessons(studentId: Int): Result<List<LessonResponse>> =
        apiCall("Błąd pobierania lekcji") { api.getStudentLessons(studentId) }

    suspend fun getTutorLessons(tutorId: Int): Result<List<LessonResponse>> =
        apiCall("Błąd pobierania lekcji") { api.getTutorLessons(tutorId) }

    suspend fun getLesson(lessonId: Int): Result<LessonResponse> =
        apiCall("Błąd pobierania lekcji") { api.getLesson(lessonId) }

    suspend fun changeLessonStatus(lessonId: Int, request: LessonStatusRequest): Result<LessonResponse> =
        apiCall("Błąd zmiany statusu") { api.changeLessonStatus(lessonId, request) }

    suspend fun updateStudentNotes(lessonId: Int, notes: String?): Result<LessonResponse> =
        apiCall("Błąd zapisu notatek") { api.updateStudentNotes(lessonId, StudentNotesRequest(notes)) }

    suspend fun updateTutorNotes(lessonId: Int, notes: String?): Result<LessonResponse> =
        apiCall("Błąd zapisu notatek") { api.updateTutorNotes(lessonId, TutorNotesRequest(notes)) }

    suspend fun updatePaymentStatus(lessonId: Int, status: PaymentStatus): Result<LessonResponse> =
        apiCall("Błąd zmiany statusu płatności") { api.updatePaymentStatus(lessonId, PaymentStatusRequest(status)) }
}
