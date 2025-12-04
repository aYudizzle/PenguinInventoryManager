package dev.ayupi.pse_new.core.datastore.di

import dev.ayupi.pse_new.core.datastore.AppSettingsFactory
import dev.ayupi.pse_new.core.datastore.SettingsDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.core.module.Module

val dataStoreModule: Module = module {
    includes(platformDataStoreModule)
    single {
        AppSettingsFactory.create(
            producePath = { get(named("dataStorePath")) }
        )
    }

    single {
        SettingsDataSource(dataStore = get())
    }
}

expect val platformDataStoreModule: Module