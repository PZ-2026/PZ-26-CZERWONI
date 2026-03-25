package pl.edu.ur.teachly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import pl.edu.ur.teachly.ui.auth.LoginScreen
import pl.edu.ur.teachly.ui.auth.RegisterScreen
import pl.edu.ur.teachly.ui.auth.SplashScreen
import pl.edu.ur.teachly.ui.home.BookingConfirmScreen
import pl.edu.ur.teachly.ui.home.BookingScreen
import pl.edu.ur.teachly.ui.home.HomeScreen
import pl.edu.ur.teachly.ui.home.TutorDetailScreen

@Composable
fun AppNavHost(
    navController    : NavHostController,
    modifier         : Modifier = Modifier,
    startDestination : AppRoute   = AppRoute.Splash,
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        modifier         = modifier,
    ) {

        // Auth
        composable<AppRoute.Splash> {
            SplashScreen(
                onLoginClick    = { navController.navigate(AppRoute.Login) },
                onRegisterClick = { navController.navigate(AppRoute.Register) },
            )
        }

        composable<AppRoute.Login> {
            LoginScreen(
                onBack    = { navController.popBackStack() },
                onSuccess = { navController.navigateToHome() },
            )
        }

        composable<AppRoute.Register> {
            RegisterScreen(
                onBack    = { navController.popBackStack() },
                onSuccess = { navController.navigateToHome() },
            )
        }

        // Home flow
        composable<AppRoute.Home> {
            HomeScreen(
                onTutorClick = { tutor ->
                    navController.navigate(AppRoute.TutorDetail(tutor.id.toString()))
                },
                onLogout = { navController.navigateToSplash() },
            )
        }

        composable<AppRoute.TutorDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.TutorDetail>()
            TutorDetailScreen(
                tutorId     = args.tutorId,
                onBack      = { navController.popBackStack() },
                onBookClick = { navController.navigate(AppRoute.Booking(args.tutorId)) },
            )
        }

        composable<AppRoute.Booking> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.Booking>()
            BookingScreen(
                tutorId   = args.tutorId,
                onBack    = { navController.popBackStack() },
                onConfirm = { bookingId, scheduledAt ->
                    navController.navigate(
                        AppRoute.BookingConfirm(
                            tutorId     = args.tutorId,
                            bookingId   = bookingId,
                            scheduledAt = scheduledAt,
                        )
                    )
                },
            )
        }

        composable<AppRoute.BookingConfirm> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.BookingConfirm>()
            BookingConfirmScreen(
                bookingId   = args.bookingId,
                scheduledAt = args.scheduledAt,
                onGoHome    = { navController.navigateToHome() },
            )
        }
    }
}

// Helpers
private fun NavHostController.navigateToHome() {
    navigate(AppRoute.Home) {
        popUpTo<AppRoute.Splash> { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToSplash() {
    navigate(AppRoute.Splash) {
        popUpTo<AppRoute.Home> { inclusive = true }
        launchSingleTop = true
    }
}