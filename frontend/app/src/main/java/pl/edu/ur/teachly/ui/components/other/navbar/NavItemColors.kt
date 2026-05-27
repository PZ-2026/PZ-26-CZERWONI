package pl.edu.ur.teachly.ui.components.other.navbar

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = colorScheme.surface,
    selectedTextColor = colorScheme.onSurface,
    indicatorColor = colorScheme.primary,
    unselectedIconColor = colorScheme.onSurfaceVariant,
    unselectedTextColor = colorScheme.onSurfaceVariant
)

