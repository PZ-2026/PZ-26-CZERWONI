package pl.edu.ur.teachly.ui.components.other.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val routeStr = currentDestination?.route ?: ""

    // Hide on Auth screens
    if (routeStr.contains("Splash") || routeStr.contains("Login") || routeStr.contains("Register")) {
        return
    }

    val isHome = routeStr.contains("Home")
    val isSearch = routeStr.contains("Search")
    val isSchedule = routeStr.contains("Schedule")
    val isProfile = routeStr.contains("Profile")

    NavigationBar(
        modifier = modifier,
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = isHome,
            onClick = {
                navController.navigate(AppRoute.Home) {
                    popUpTo(AppRoute.Home) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = stringResource(R.string.nav_home)
                )
            },
            label = { Text(stringResource(R.string.nav_home)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isSearch,
            onClick = {
                navController.navigate(AppRoute.Search) {
                    popUpTo(AppRoute.Search) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.nav_search)
                )
            },
            label = { Text(stringResource(R.string.nav_search)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isSchedule,
            onClick = {
                navController.navigate(AppRoute.Schedule) {
                    popUpTo(AppRoute.Home) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.nav_schedule)
                )
            },
            label = { Text(stringResource(R.string.nav_schedule)) },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = isProfile,
            onClick = {
                navController.navigate(AppRoute.Profile) {
                    popUpTo(AppRoute.Home) { inclusive = false }
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
