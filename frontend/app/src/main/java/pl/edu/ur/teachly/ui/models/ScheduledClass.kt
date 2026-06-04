package pl.edu.ur.teachly.ui.models

import java.time.LocalDate
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus

data class ScheduledClass(
    val id: String,
    val subject: String,
    val tutorName: String,
    val studentName: String,
    val tutorAvatarUrl: String? = null,
    val studentAvatarUrl: String? = null,
    val day: LocalDate,
    val time: String,
    val durationMinutes: Int,
    val status: LessonStatus,
    val format: LessonFormat,
    val paymentStatus: PaymentStatus
)
