package pl.edu.ur.teachly.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.ui.admin.views.AdminDashboardScreen
import pl.edu.ur.teachly.ui.admin.views.AdminDataScreen
import pl.edu.ur.teachly.ui.admin.views.AdminHolidaysScreen
import pl.edu.ur.teachly.ui.admin.views.AdminLessonsScreen
import pl.edu.ur.teachly.ui.admin.views.AdminReviewsScreen
import pl.edu.ur.teachly.ui.admin.views.AdminSubjectsScreen
import pl.edu.ur.teachly.ui.admin.views.AdminTutorsScreen
import pl.edu.ur.teachly.ui.admin.views.AdminUsersScreen
import pl.edu.ur.teachly.ui.auth.views.LoginScreen
import pl.edu.ur.teachly.ui.auth.views.RegisterScreen
import pl.edu.ur.teachly.ui.auth.views.SplashScreen
import pl.edu.ur.teachly.ui.availability.views.AvailabilityScreen
import pl.edu.ur.teachly.ui.booking.views.BookingConfirmScreen
import pl.edu.ur.teachly.ui.booking.views.BookingScreen
import pl.edu.ur.teachly.ui.home.views.HomeScreen
import pl.edu.ur.teachly.ui.lesson.views.LessonDetailScreen
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorProfileViewModel
import pl.edu.ur.teachly.ui.profile.views.AdminProfileScreen
import pl.edu.ur.teachly.ui.profile.views.ProfileEditScreen
import pl.edu.ur.teachly.ui.profile.views.StudentProfileScreen
import pl.edu.ur.teachly.ui.profile.views.TutorProfileScreen
import pl.edu.ur.teachly.ui.review.views.AllReviewsScreen
import pl.edu.ur.teachly.ui.schedule.views.ScheduleScreen
import pl.edu.ur.teachly.ui.search.views.SearchScreen
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
            val tokenManager = koinInject<TokenManager>()
            val scope = rememberCoroutineScope()
            LoginScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    scope.launch {
                        val role = tokenManager.roleFlow.first()
                        if (role == "ADMIN") navController.navigateToAdminDashboard()
                        else navController.navigateToHome()
                    }
                },
            )
        }

        composable<AppRoute.Register> {
            val tokenManager = koinInject<TokenManager>()
            val scope = rememberCoroutineScope()
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    scope.launch {
                        val role = tokenManager.roleFlow.first()
                        if (role == "ADMIN") navController.navigateToAdminDashboard()
                        else navController.navigateToHome()
                    }
                },
            )
        }

        // Home
        composable<AppRoute.Home> {
            HomeScreen(
                onSearchClick = { navController.navigate(AppRoute.Search) },
                onLessonClick = { lessonId -> navController.navigate(AppRoute.LessonDetail(lessonId)) },
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
                onSeeAllReviews = {
                    navController.navigate(AppRoute.AllReviews(args.tutorId, ""))
                },
            )
        }

        composable<AppRoute.Booking> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.Booking>()
            BookingScreen(
                tutorId = args.tutorId,
                onBack = { navController.popBackStack() },
                onConfirm = { tutorName, subjectName, lessonDate, timeFrom, timeTo, format, amount ->
                    navController.navigate(
                        AppRoute.BookingConfirm(
                            tutorName = tutorName,
                            subjectName = subjectName,
                            lessonDate = lessonDate,
                            timeFrom = timeFrom,
                            timeTo = timeTo,
                            format = format,
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
                format = args.format,
                amount = args.amount,
                onGoHome = { navController.navigateToHome() },
            )
        }

        // Lesson detail
        composable<AppRoute.LessonDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.LessonDetail>()
            LessonDetailScreen(
                lessonId = args.lessonId,
                onBack = { navController.popBackStack() },
                onGoToTutor = { tutorId -> navController.navigate(AppRoute.TutorDetail(tutorId)) },
                onRebook = { tutorId -> navController.navigate(AppRoute.Booking(tutorId.toString())) },
            )
        }

        // Schedule
        composable<AppRoute.Schedule> {
            ScheduleScreen(
                onBack = { navController.popBackStack() },
                onLessonClick = { lessonId -> navController.navigate(AppRoute.LessonDetail(lessonId)) },
            )
        }

        // Profile
        composable<AppRoute.Profile> { entry ->
            val tokenManager = koinInject<TokenManager>()
            val role by tokenManager.roleFlow.collectAsState(initial = null)
            val userId by tokenManager.userIdFlow.collectAsState(initial = null)

            val profileViewModel: ProfileViewModel = koinViewModel(viewModelStoreOwner = entry)
            val tutorViewModel: TutorProfileViewModel = koinViewModel(viewModelStoreOwner = entry)

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
                    onSeeAllReviews = {
                        userId?.let {
                            navController.navigate(AppRoute.AllReviews(it, ""))
                        }
                    },
                    onAvailabilityClick = {
                        userId?.let {
                            navController.navigate(AppRoute.TutorAvailability(it))
                        }
                    },
                    viewModel = tutorViewModel,
                )

                "ADMIN" -> AdminProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = { navController.navigateToSplash() },
                    viewModel = profileViewModel,
                )

                else -> StudentProfileScreen(
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(AppRoute.ProfileEdit) },
                    onLogout = { navController.navigateToSplash() },
                    viewModel = profileViewModel,
                )
            }
        }

        composable<AppRoute.ProfileEdit> { entry ->
            val profileEntry =
                remember(entry) { navController.getBackStackEntry<AppRoute.Profile>() }
            val profileViewModel: ProfileViewModel =
                koinViewModel(viewModelStoreOwner = profileEntry)
            ProfileEditScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                viewModel = profileViewModel,
            )
        }

        composable<AppRoute.TutorProfile> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.TutorProfile>()
            TutorProfileScreen(
                tutorId = args.tutorId.toString(),
                isMyProfile = false,
                onBack = { navController.popBackStack() },
                onEditClick = {},
                onLogout = {},
                onSeeAllReviews = {
                    navController.navigate(AppRoute.AllReviews(args.tutorId, ""))
                },
            )
        }

        composable<AppRoute.AllReviews> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.AllReviews>()
            AllReviewsScreen(
                tutorId = args.tutorId,
                tutorName = args.tutorName,
                onBack = { navController.popBackStack() },
            )
        }

        composable<AppRoute.TutorAvailability> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.TutorAvailability>()
            AvailabilityScreen(
                tutorId = args.tutorId,
                onBack = { navController.popBackStack() },
            )
        }

        // Admin screens
        composable<AppRoute.AdminDashboard> {
            AdminDashboardScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoute.AdminDashboard) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<AppRoute.AdminUsers> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.AdminUsers>()
            AdminUsersScreen(initialRoleFilter = args.roleFilter)
        }

        composable<AppRoute.AdminLessons> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.AdminLessons>()
            AdminLessonsScreen(initialStatusFilter = args.statusFilter)
        }

        composable<AppRoute.AdminData> { backStackEntry ->
            val args = backStackEntry.toRoute<AppRoute.AdminData>()
            AdminDataScreen(
                initialTab = args.initialTab,
                initialSubjectTab = args.initialSubjectTab
            )
        }

        composable<AppRoute.AdminHolidays> {
            AdminHolidaysScreen()
        }

        composable<AppRoute.AdminSubjects> {
            AdminSubjectsScreen()
        }

        composable<AppRoute.AdminTutors> {
            AdminTutorsScreen()
        }

        composable<AppRoute.AdminReviews> {
            AdminReviewsScreen()
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

private fun NavHostController.navigateToAdminDashboard() {
    navigate(AppRoute.AdminDashboard) {
        popUpTo<AppRoute.Splash> { inclusive = true }
        launchSingleTop = true
    }
}
