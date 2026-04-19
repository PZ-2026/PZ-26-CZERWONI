package pl.edu.ur.teachly.ui.components

import java.time.LocalDate
import kotlin.math.roundToInt

data class Tutor(
    val id: Int,
    val name: String,
    val initials: String,
    val subjects: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val pricePerHour: Int,
    val tags: List<String>,
    val isOnline: Boolean,
    val nearestSlots: List<String>,
    val bio: String = "",
    val lessonCount: Int = 0,
)

data class CalendarDay(val shortName: String, val dayNumber: String)

data class BookingResult(
    val tutor: Tutor,
    val day: CalendarDay,
    val timeSlot: String,
    val durationMinutes: Int,
) {
    val totalPrice: Int get() = (tutor.pricePerHour * durationMinutes / 60.0).roundToInt()
}

data class ScheduledClass(
    val id: String,
    val subject: String,
    val tutorName: String,
    val studentName: String,
    val day: LocalDate,
    val time: String,
    val durationMinutes: Int,
    val status: String,
)

data class Review(
    val authorName: String,
    val text: String,
    val rating: Int,
)

// UI constants
val DURATION_OPTIONS = listOf(30, 60, 90)
