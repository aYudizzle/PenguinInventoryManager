package dev.ayupi.pse_new.core.datastore.di

import android.content.Context
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single(named("dataStorePath")) {
        val context = get<Context>()
        context.filesDir.resolve("datastore").absolutePath
    }
}