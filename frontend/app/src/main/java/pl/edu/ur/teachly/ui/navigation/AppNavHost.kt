package pl.edu.ur.teachly.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.koinInject
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.ui.auth.views.LoginScreen
import pl.edu.ur.teachly.ui.auth.views.RegisterScreen
import pl.edu.ur.teachly.ui.auth.views.SplashScreen
import pl.edu.ur.teachly.ui.home.views.BookingConfirmScreen
import pl.edu.ur.teachly.ui.home.views.BookingScreen
import pl.edu.ur.teachly.ui.home.views.HomeScreen
import pl.edu.ur.teachly.ui.home.views.SearchScreen
import pl.edu.ur.teachly.ui.profile.views.ProfileEditScreen
import pl.edu.ur.teachly.ui.profile.views.StudentProfileScreen
import pl.edu.ur.teachly.ui.profile.views.TutorProfileScreen
import pl.edu.ur.teachly.ui.schedule.views.ScheduleScreen
import pl.edu.ur.teachly.ui.tutor.views.TutorDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: AppRoute = AppRoute.Splash,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        // Auth
        composable<AppRoute.Splash> {
            SplashScreen(
                onLoginClick = { navController.navigate(AppRoute.Login) },
                onRegisterClick = { navController.navigate(AppRoute.Register) },
            )
        }

        composable<AppRoute.Login> {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.navigateToHome() },
            )
        }

        composable<AppRoute.Register> {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.navigateToHome() },
            )
        }

        // Home
        composable<AppRoute.Home> {
            HomeScreen(
                onSearchClick = { navController.navigate(AppRoute.Search) },
            )
        }

        // Search / Tutor list
        composable<AppRoute.Search> {
            SearchScreen(
                onTutorClick = { tutor ->
                    navController.navigate(AppRoute.TutorDetail(tutor.id))
                },
            )
        }

        composable<AppRoute.TutorDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.TutorDetail>()
            TutorDetailScreen(
                tutorId = args.tutorId.toString(),
                onBack = { navController.popBackStack() },
                onBookClick = { navController.navigate(AppRoute.Booking(args.tutorId.toString())) },
            )
        }

        composable<AppRoute.Booking> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.Booking>()
            BookingScreen(
                tutorId = args.tutorId,
                onBack = { navController.popBackStack() },
                onConfirm = { tutorName, subjectName, lessonDate, timeFrom, timeTo, amount ->
                    navController.navigate(
                        AppRoute.BookingConfirm(
                            tutorName = tutorName,
                            subjectName = subjectName,
                            lessonDate = lessonDate,
                            timeFrom = timeFrom,
                            timeTo = timeTo,
                            amount = amount,
                        )
                    )
                },
            )
        }

        composable<AppRoute.BookingConfirm> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.BookingConfirm>()
            BookingConfirmScreen(
                tutorName = args.tutorName,
                subjectName = args.subjectName,
                lessonDate = args.lessonDate,
                timeFrom = args.timeFrom,
                timeTo = args.timeTo,
                amount = args.amount,
                onGoHome = { navController.navigateToHome() },
            )
        }

        // Schedule
        composable<AppRoute.Schedule> {
            ScheduleScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Profile
        composable<AppRoute.Profile> {
            val tokenManager = koinInject<TokenManager>()
            val role by tokenManager.roleFlow.collectAsState(initial = null)
            val userId by tokenManager.userIdFlow.collectAsState(initial = null)

            when (role) {
                null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                "TUTOR" -> TutorProfileScreen(
                    tutorId = userId?.toString() ?: "",
                    isMyProfile = true,
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(AppRoute.ProfileEdit) },
                    onLogout = { navController.navigateToSplash() },
                )

                else -> StudentProfileScreen(
                    isStudent = true,
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(AppRoute.ProfileEdit) },
                    onLogout = { navController.navigateToSplash() },
                )
            }
        }

        composable<AppRoute.ProfileEdit> {
            ProfileEditScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
            )
        }

        // Tutor profile (viewed from search/schedule)
        composable<AppRoute.TutorProfile> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.TutorProfile>()
            TutorProfileScreen(
                tutorId = args.tutorId.toString(),
                isMyProfile = false,
                onBack = { navController.popBackStack() },
                onEditClick = {},
                onLogout = {},
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
