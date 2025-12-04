package dev.ayupi.pse_new.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun getEngine(): HttpClientEngineFactory<*> = CIO