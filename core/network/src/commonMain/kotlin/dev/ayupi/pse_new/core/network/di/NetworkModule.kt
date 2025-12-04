package dev.ayupi.pse_new.core.network.di

import dev.ayupi.pse_new.core.network.BuildKonfig
import dev.ayupi.pse_new.core.network.KtorClientFactory
import dev.ayupi.pse_new.core.network.StorageApi
import org.koin.dsl.module

val networkModule = module {
    single { KtorClientFactory(
        baseUrl = BuildKonfig.API_BASE_URL,
        apiKey = BuildKonfig.API_KEY
    ) }

    single {
        get<KtorClientFactory>().create()
    }

    single { StorageApi(client = get()) }
}