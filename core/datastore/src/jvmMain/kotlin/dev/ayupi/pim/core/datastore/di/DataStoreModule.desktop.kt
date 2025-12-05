package dev.ayupi.pim.core.datastore.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

actual val platformDataStoreModule: Module = module {
    single(named("dataStorePath")) {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".pse_app")
        if(!appDir.exists()) appDir.mkdirs()
        File(appDir, "pse_app_settings.json").absolutePath
    }
}