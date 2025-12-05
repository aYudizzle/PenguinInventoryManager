package dev.ayupi.pim.core.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.ayupi.pim.core.database.AppDatabase
import dev.ayupi.pim.core.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDataStoreModule: Module
val databaseModule = module {
    includes(platformDataStoreModule)
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // 2. DAO bereitstellen
    single { get<AppDatabase>().storageDao }
    single { get<AppDatabase>().inventoryDao }
    single { get<AppDatabase>().itemDao }
}