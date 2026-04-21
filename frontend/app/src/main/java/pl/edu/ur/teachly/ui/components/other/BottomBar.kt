package pl.edu.ur.teachly.ui.components.other

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
fun BottomBar(
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
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.surface,
                selectedTextColor = colorScheme.onSurface,
                indicatorColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant,
                unselectedTextColor = colorScheme.onSurfaceVariant
            )
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
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.surface,
                selectedTextColor = colorScheme.onSurface,
                indicatorColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant,
                unselectedTextColor = colorScheme.onSurfaceVariant
            )
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
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.surface,
                selectedTextColor = colorScheme.onSurface,
                indicatorColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant,
                unselectedTextColor = colorScheme.onSurfaceVariant
            )
        )

    }
}
