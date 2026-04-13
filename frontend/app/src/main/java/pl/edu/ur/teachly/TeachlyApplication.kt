package pl.edu.ur.teachly

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pl.edu.ur.teachly.data.di.appModule
import pl.edu.ur.teachly.data.di.networkModule

class TeachlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TeachlyApplication)
            modules(networkModule, appModule)
        }
    }
}