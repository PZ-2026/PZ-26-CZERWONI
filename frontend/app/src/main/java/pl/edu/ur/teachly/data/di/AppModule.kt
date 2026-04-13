package pl.edu.ur.teachly.data.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.repository.AuthRepository
import pl.edu.ur.teachly.ui.auth.viewmodels.LoginViewModel
import pl.edu.ur.teachly.ui.auth.viewmodels.RegisterViewModel

val appModule = module {
    single { TokenManager(androidContext()) }
    single { AuthRepository(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}
