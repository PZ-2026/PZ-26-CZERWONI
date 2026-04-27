package pl.edu.ur.teachly.ui.components.other.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.navigation.AppRoute

@Composable
fun AdminBottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val routeStr = navBackStackEntry?.destination?.route ?: ""

    // Hide on Auth screens
    if (routeStr.contains("Splash") || routeStr.contains("Login") || routeStr.contains("Register")) {
        return
    }

    val isDashboard = routeStr.contains("AdminDashboard")
    val isUsers = routeStr.contains("AdminUser")
    val isLessons = routeStr.contains("AdminLesson")
    val isData = routeStr.contains("AdminData") || routeStr.contains("AdminHoliday")
            || routeStr.contains("AdminSubject") || routeStr.contains("AdminTutor")
            || routeStr.contains("AdminReview")
    val isProfile = routeStr.contains("Profile")

    NavigationBar(
        modifier = modifier,
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = isDashboard,
            onClick = {
                navController.navigate(AppRoute.AdminDashboard) {
                    popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isUsers,
            onClick = {
                navController.navigate(AppRoute.AdminUsers()) {
                    popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.People, contentDescription = "Użytkownicy") },
            label = { Text("Użytkownicy") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isLessons,
            onClick = {
                navController.navigate(AppRoute.AdminLessons()) {
                    popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Lekcje") },
            label = { Text("Lekcje") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isData,
            onClick = {
                navController.navigate(AppRoute.AdminData()) {
                    popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Dane") },
            label = { Text("Dane") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isProfile,
            onClick = {
                navController.navigate(AppRoute.Profile) {
                    popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = stringResource(R.string.nav_profile)
                )
            },
            label = { Text(stringResource(R.string.nav_profile)) },
            colors = navItemColors()
        )
    }
}


