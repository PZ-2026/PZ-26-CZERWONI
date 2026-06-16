package pl.edu.ur.teachly.ui.models

data class BookingResult(val tutor: Tutor, val day: CalendarDay, val timeSlot: String, val durationMinutes: Int)

val BookingResult.totalPrice: Double
    get() = tutor.pricePerHour * durationMinutes / 60.0
