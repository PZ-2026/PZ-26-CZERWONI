package pl.edu.ur.teachly.data.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.repository.AuthRepository
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.data.repository.UserRepository
import pl.edu.ur.teachly.ui.auth.viewmodels.LoginViewModel
import pl.edu.ur.teachly.ui.auth.viewmodels.RegisterViewModel
import pl.edu.ur.teachly.ui.booking.viewmodels.BookingViewModel
import pl.edu.ur.teachly.ui.home.viewmodels.HomeViewModel
import pl.edu.ur.teachly.ui.lesson.viewmodels.LessonDetailViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorProfileViewModel
import pl.edu.ur.teachly.ui.schedule.viewmodels.ScheduleViewModel
import pl.edu.ur.teachly.ui.search.viewmodels.SearchViewModel
import pl.edu.ur.teachly.ui.tutor.viewmodels.TutorDetailViewModel

val appModule = module {
    // Core
    single { TokenManager(androidContext()) }

    // Repositories
    single { AuthRepository(get(), get()) }
    single { TutorRepository(get()) }
    single { LessonRepository(get()) }
    single { UserRepository(get()) }
    single { ReviewRepository(get()) }
    single { SubjectRepository(get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { BookingViewModel(get(), get(), get()) }
    viewModel { ScheduleViewModel(get(), get()) }
    viewModel { TutorDetailViewModel(get(), get()) }
    viewModel { LessonDetailViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { TutorProfileViewModel(get(), get(), get()) }
}
