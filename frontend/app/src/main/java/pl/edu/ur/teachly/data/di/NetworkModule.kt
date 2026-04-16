package pl.edu.ur.teachly.data.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pl.edu.ur.teachly.BuildConfig
import pl.edu.ur.teachly.data.remote.AuthApiService
import pl.edu.ur.teachly.data.remote.AuthInterceptor
import pl.edu.ur.teachly.data.remote.HolidayApiService
import pl.edu.ur.teachly.data.remote.LessonApiService
import pl.edu.ur.teachly.data.remote.ReviewApiService
import pl.edu.ur.teachly.data.remote.SubjectApiService
import pl.edu.ur.teachly.data.remote.TutorApiService
import pl.edu.ur.teachly.data.remote.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        AuthInterceptor(get())
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApiService::class.java) }
    single { get<Retrofit>().create(TutorApiService::class.java) }
    single { get<Retrofit>().create(LessonApiService::class.java) }
    single { get<Retrofit>().create(ReviewApiService::class.java) }
    single { get<Retrofit>().create(SubjectApiService::class.java) }
    single { get<Retrofit>().create(UserApiService::class.java) }
    single { get<Retrofit>().create(HolidayApiService::class.java) }
}
