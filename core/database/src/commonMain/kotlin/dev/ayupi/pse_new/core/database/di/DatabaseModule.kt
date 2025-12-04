package dev.ayupi.pse_new.core.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.ayupi.pse_new.core.database.AppDatabase
import dev.ayupi.pse_new.core.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDataStoreModule: Module
val databaseModule = module {
    includes(platformDataStoreModule)
    // 1. Datenbank erstellen
    single {
        // Wir holen die Factory (die wir in androidMain/jvmMain bereitstellen)
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver()) // <--- DER WICHTIGSTE KMP SCHRITT!
            .fallbackToDestructiveMigration(true) // <--- DIESE ZEILE EINFÜGEN
            .build()
    }

    // 2. DAO bereitstellen
    single { get<AppDatabase>().storageDao }
    single { get<AppDatabase>().inventoryDao }
    single { get<AppDatabase>().itemDao }
}