package dev.ayupi.pim.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun getEngine(): HttpClientEngineFactory<*> = CIO