package pl.edu.ur.teachly.ui.components.other.navbar

private val hiddenBottomNavRouteMarkers = listOf(
    "Splash",
    "Login",
    "Register",
    "TutorSetup",
    "Booking",
    "BookingConfirm",
    "LessonDetail",
    "TutorDetail",
    "AllReviews",
    "TutorAvailability",
    "TutorProfile",
    "ProfileEdit",
    "AdminUsers",
    "AdminLessons",
    "AdminUserEdit",
    "AdminLessonEdit",
    "AdminTutorEdit"
)

fun shouldShowBottomNav(route: String?): Boolean {
    if (route.isNullOrBlank()) return false
    return hiddenBottomNavRouteMarkers.none { route.contains(it) }
}
