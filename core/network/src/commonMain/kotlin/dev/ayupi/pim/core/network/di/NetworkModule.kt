package dev.ayupi.pim.core.network.di

import dev.ayupi.pim.core.network.BuildKonfig
import dev.ayupi.pim.core.network.KtorClientFactory
import dev.ayupi.pim.core.network.StorageApi
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