package pl.edu.ur.teachly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.navigation.AppNavHost
import pl.edu.ur.teachly.ui.components.other.navbar.AdminBottomNavBar
import pl.edu.ur.teachly.ui.components.other.navbar.BottomNavBar
import pl.edu.ur.teachly.ui.theme.TeachlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TeachlyTheme {
                val navController = rememberNavController()
                val tokenManager = koinInject<TokenManager>()
                val role by tokenManager.roleFlow.collectAsState(initial = null)

                Scaffold(
                    bottomBar = {
                        if (role == "ADMIN") {
                            AdminBottomNavBar(navController = navController)
                        } else {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}