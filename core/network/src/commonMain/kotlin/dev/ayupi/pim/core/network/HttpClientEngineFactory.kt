package dev.ayupi.pim.core.network

import io.ktor.client.engine.HttpClientEngineFactory

expect fun getEngine(): HttpClientEngineFactory<*>