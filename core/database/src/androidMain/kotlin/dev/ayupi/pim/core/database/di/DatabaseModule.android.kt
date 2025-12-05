package dev.ayupi.pim.core.database.di

import dev.ayupi.pim.core.database.AndroidDatabaseFactory
import dev.ayupi.pim.core.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single<DatabaseFactory> { AndroidDatabaseFactory(
        context = get()
    ) }
}