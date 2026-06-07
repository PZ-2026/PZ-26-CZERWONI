package pl.edu.ur.teachly

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import pl.edu.ur.teachly.data.local.SessionManager
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.navigation.AppNavHost
import pl.edu.ur.teachly.navigation.navigateToSplash
import pl.edu.ur.teachly.ui.components.other.navbar.AdminBottomNavBar
import pl.edu.ur.teachly.ui.components.other.navbar.BottomNavBar
import pl.edu.ur.teachly.ui.components.other.navbar.shouldShowBottomNav
import pl.edu.ur.teachly.ui.theme.TeachlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        window.isNavigationBarContrastEnforced = false

        setContent {
            TeachlyTheme {
                val navController = rememberNavController()
                val tokenManager = koinInject<TokenManager>()
                val sessionManager = koinInject<SessionManager>()
                val role by tokenManager.roleFlow.collectAsState(initial = null)
                val currentRoute by navController.currentBackStackEntryAsState()
                val route = currentRoute?.destination?.route
                val showBottomBar = shouldShowBottomNav(route)

                LaunchedEffect(sessionManager) {
                    sessionManager.sessionExpired.collect {
                        navController.navigateToSplash()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = colorScheme.background,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (showBottomBar) {
                            if (role == "ADMIN") {
                                AdminBottomNavBar(navController = navController)
                            } else {
                                BottomNavBar(navController = navController, role = role)
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                }
            }
        }
    }
}
