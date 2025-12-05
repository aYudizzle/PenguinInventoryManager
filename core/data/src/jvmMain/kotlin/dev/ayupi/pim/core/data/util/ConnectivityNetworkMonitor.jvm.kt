package dev.ayupi.pim.core.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetSocketAddress
import java.net.Socket

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ConnectivityNetworkMonitor actual constructor() : NetworkMonitor {
    override val isOnline: Flow<Boolean> = flow {
        while(true) {
            val isConnected = try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress("8.8.8.8", 53), 2000)
                    true
                }
            } catch (e: Exception) {
                false
            }
            emit(isConnected)
            delay(10000)
        }
    }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()
}