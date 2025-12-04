package dev.ayupi.pse_new

import android.app.Application
import dev.ayupi.pse_new.di.appModule
import dev.ayupi.pse_new.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PSEApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            config = {
                androidContext(this@PSEApplication)
                androidLogger()
            }
        )
    }
}