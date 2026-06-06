package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.AdminLessonUpdateRequest
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonRequest
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.model.LessonStatusRequest
import pl.edu.ur.teachly.data.model.PaymentStatusRequest
import pl.edu.ur.teachly.data.model.StudentNotesRequest
import pl.edu.ur.teachly.data.model.TutorNotesRequest
import pl.edu.ur.teachly.data.remote.LessonApiService

class LessonRepository(private val api: LessonApiService) {

    suspend fun getAllLessons(
        query: String? = null,
        status: LessonStatus? = null,
        paymentStatus: PaymentStatus? = null,
        format: LessonFormat? = null,
        upcoming: Boolean? = null
    ): Result<List<LessonResponse>> = try {
        val response = api.getAllLessons(query, status, paymentStatus, format, upcoming)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd pobierania lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun adminUpdateLesson(lessonId: Int, request: AdminLessonUpdateRequest): Result<LessonResponse> = try {
        val response = api.adminUpdateLesson(lessonId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd aktualizacji lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun createLesson(studentId: Int, request: LessonRequest): Result<LessonResponse> = try {
        val response = api.createLesson(studentId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd tworzenia lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun getStudentLessons(studentId: Int): Result<List<LessonResponse>> = try {
        val response = api.getStudentLessons(studentId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd pobierania lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun getTutorLessons(tutorId: Int): Result<List<LessonResponse>> = try {
        val response = api.getTutorLessons(tutorId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd pobierania lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun getLesson(lessonId: Int): Result<LessonResponse> = try {
        val response = api.getLesson(lessonId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd pobierania lekcji"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun changeLessonStatus(lessonId: Int, request: LessonStatusRequest): Result<LessonResponse> = try {
        val response = api.changeLessonStatus(lessonId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd zmiany statusu"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun updateStudentNotes(lessonId: Int, notes: String?): Result<LessonResponse> = try {
        val response = api.updateStudentNotes(lessonId, StudentNotesRequest(notes))
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd zapisu notatek"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun updateTutorNotes(lessonId: Int, notes: String?): Result<LessonResponse> = try {
        val response = api.updateTutorNotes(lessonId, TutorNotesRequest(notes))
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd zapisu notatek"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }

    suspend fun updatePaymentStatus(
        lessonId: Int,
        status: pl.edu.ur.teachly.data.model.PaymentStatus
    ): Result<LessonResponse> = try {
        val response = api.updatePaymentStatus(lessonId, PaymentStatusRequest(status))
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd zmiany statusu płatności"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Brak połączenia z serwerem"))
    }
}
