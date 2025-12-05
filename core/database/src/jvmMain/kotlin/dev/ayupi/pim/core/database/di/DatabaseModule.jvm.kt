package dev.ayupi.pim.core.database.di

import dev.ayupi.pim.core.database.DatabaseFactory
import dev.ayupi.pim.core.database.DesktopDatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single<DatabaseFactory> { DesktopDatabaseFactory() }
}