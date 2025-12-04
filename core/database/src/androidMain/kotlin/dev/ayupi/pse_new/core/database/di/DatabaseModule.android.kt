package dev.ayupi.pse_new.core.database.di

import dev.ayupi.pse_new.core.database.AndroidDatabaseFactory
import dev.ayupi.pse_new.core.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single<DatabaseFactory> { AndroidDatabaseFactory(
        context = get()
    ) }
}