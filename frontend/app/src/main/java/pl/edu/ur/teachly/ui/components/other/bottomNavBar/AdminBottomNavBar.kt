package pl.edu.ur.teachly.ui.components.other.bottomNavBar

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

@Composable
fun AdminBottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val routeStr = navBackStackEntry?.destination?.route ?: ""

    if (routeStr.contains("Splash") || routeStr.contains("Login") || routeStr.contains("Register")) {
        return
    }

    val isDashboard = routeStr.contains("AdminDashboard")
    val isUsers = routeStr.contains("AdminUser")
    val isLessons = routeStr.contains("AdminLesson")
    val isData = routeStr.contains("AdminHoliday") || routeStr.contains("AdminSubject") ||
            routeStr.contains("AdminTutor") || routeStr.contains("AdminReview")
    val isProfile = routeStr.contains("AdminProfile")

    NavigationBar(
        modifier = modifier,
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = isDashboard,
            onClick = { /* TODO: Handle admin */ },
            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = stringResource(R.string.dashboard)
                )
            },
            label = { Text(stringResource(R.string.dashboard)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isUsers,
            onClick = { /* TODO: Handle admin */ },
            icon = {
                Icon(
                    Icons.Default.People,
                    contentDescription = stringResource(R.string.users)
                )
            },
            label = { Text(stringResource(R.string.users)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isLessons,
            onClick = { /* TODO: Handle admin */ },
            icon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.lessons)
                )
            },
            label = { Text(stringResource(R.string.lessons)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isData,
            onClick = { /* TODO: Handle admin */ },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = stringResource(R.string.data)
                )
            },
            label = { Text(stringResource(R.string.data)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isProfile,
            onClick = { /* TODO: Handle admin */ },
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
