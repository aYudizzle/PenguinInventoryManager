package dev.ayupi.pse_new.core.database.di

import dev.ayupi.pse_new.core.database.DatabaseFactory
import dev.ayupi.pse_new.core.database.DesktopDatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single<DatabaseFactory> { DesktopDatabaseFactory() }
}