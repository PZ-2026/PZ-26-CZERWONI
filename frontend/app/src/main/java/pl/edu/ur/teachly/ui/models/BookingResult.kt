package pl.edu.ur.teachly.ui.models

import kotlin.math.roundToInt

data class BookingResult(
    val tutor: Tutor,
    val day: CalendarDay,
    val timeSlot: String,
    val durationMinutes: Int,
)

val BookingResult.totalPrice: Int
    get() = (tutor.pricePerHour * durationMinutes / 60.0).roundToInt()
