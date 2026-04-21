package pl.edu.ur.teachly.ui.models

import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import java.time.LocalDate

data class ScheduledClass(
    val id: String,
    val subject: String,
    val tutorName: String,
    val studentName: String,
    val day: LocalDate,
    val time: String,
    val durationMinutes: Int,
    val status: LessonStatus,
    val format: LessonFormat,
    val paymentStatus: PaymentStatus,
)
