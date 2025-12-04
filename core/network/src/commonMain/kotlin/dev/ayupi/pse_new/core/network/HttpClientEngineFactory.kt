package dev.ayupi.pse_new.core.network

import io.ktor.client.engine.HttpClientEngineFactory

expect fun getEngine(): HttpClientEngineFactory<*>