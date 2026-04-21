package pl.edu.ur.teachly.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    // Auth
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute

    // Home
    @Serializable
    data object Home : AppRoute

    // Search
    @Serializable
    data object Search : AppRoute

    // Tutor details
    @Serializable
    data class TutorDetail(val tutorId: Int) : AppRoute

    // Booking flow
    @Serializable
    data class Booking(val tutorId: String) : AppRoute

    @Serializable
    data class BookingConfirm(
        val tutorName: String,
        val subjectName: String,
        val lessonDate: String,
        val timeFrom: String,
        val timeTo: String,
        val amount: String,
    ) : AppRoute

    // Lesson detail
    @Serializable
    data class LessonDetail(val lessonId: Int) : AppRoute

    // Schedule
    @Serializable
    data object Schedule : AppRoute

    // Profile
    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object ProfileEdit : AppRoute

    @Serializable
    data class TutorProfile(val tutorId: Int) : AppRoute
}
