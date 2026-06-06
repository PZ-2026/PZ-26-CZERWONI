package pl.edu.ur.teachly.ui.components.other.navbar

private val hiddenBottomNavRouteMarkers = listOf(
    "Splash",
    "Login",
    "Register",
    "TutorSetup"
)

fun shouldShowBottomNav(route: String?): Boolean {
    if (route.isNullOrBlank()) return false
    return hiddenBottomNavRouteMarkers.none { route.contains(it) }
}
