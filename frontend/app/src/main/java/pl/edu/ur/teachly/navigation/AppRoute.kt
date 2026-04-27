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
        val format: String,
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

    @Serializable
    data class AllReviews(val tutorId: Int, val tutorName: String) : AppRoute

    // Admin
    @Serializable
    data object AdminDashboard : AppRoute

    @Serializable
    data class AdminUsers(val roleFilter: String? = null) : AppRoute

    @Serializable
    data class AdminUserEdit(val userId: Int) : AppRoute

    @Serializable
    data class AdminLessons(val statusFilter: String? = null) : AppRoute

    @Serializable
    data class AdminLessonEdit(val lessonId: Int) : AppRoute

    @Serializable
    data class AdminData(val initialTab: Int = 0, val initialSubjectTab: Int = 0) : AppRoute

    @Serializable
    data object AdminHolidays : AppRoute

    @Serializable
    data object AdminSubjects : AppRoute

    @Serializable
    data object AdminTutors : AppRoute

    @Serializable
    data class AdminTutorEdit(val tutorId: Int) : AppRoute

    @Serializable
    data object AdminReviews : AppRoute
}
