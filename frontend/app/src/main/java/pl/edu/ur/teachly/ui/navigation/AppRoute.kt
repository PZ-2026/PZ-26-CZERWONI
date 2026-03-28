package pl.edu.ur.teachly.ui.navigation

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

    // Home flow
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class TutorDetail(val tutorId: Int) : AppRoute

    @Serializable
    data class Booking(val tutorId: String) : AppRoute

    @Serializable
    data class BookingConfirm(
        val tutorId: String,
        val bookingId: String,
        val scheduledAt: String,
    ) : AppRoute

    // Schedule
    @Serializable
    data object Schedule : AppRoute
  
    // Profile
    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object ProfileEdit : AppRoute
}