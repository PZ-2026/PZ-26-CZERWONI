package pl.edu.ur.teachly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.edu.ur.teachly.data.repository.AuthRepository

@Composable
fun rememberLogoutAction(navController: NavHostController): () -> Unit {
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()
    return remember(navController) {
        {
            scope.launch {
                authRepository.logout()
                navController.navigateToSplash()
            }
        }
    }
}

fun NavHostController.navigateToSplash() {
    navigate(AppRoute.Splash) {
        popUpTo<AppRoute.Splash> { inclusive = true }
        launchSingleTop = true
    }
}
