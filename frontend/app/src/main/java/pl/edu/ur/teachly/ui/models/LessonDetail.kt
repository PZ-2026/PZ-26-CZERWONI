package pl.edu.ur.teachly.ui.models

import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import java.time.LocalDate
import java.time.LocalTime

data class LessonDetail(
    val id: Int,
    val subjectName: String,
    val tutorId: Int,
    val tutorFirstName: String,
    val tutorLastName: String,
    val studentId: Int,
    val studentFirstName: String,
    val studentLastName: String,
    val lessonDate: LocalDate,
    val timeFrom: LocalTime,
    val timeTo: LocalTime,
    val format: LessonFormat,
    val lessonStatus: LessonStatus,
    val tutorNotes: String?,
    val studentNotes: String?,
    val amount: Double,
    val paymentStatus: PaymentStatus,
)
