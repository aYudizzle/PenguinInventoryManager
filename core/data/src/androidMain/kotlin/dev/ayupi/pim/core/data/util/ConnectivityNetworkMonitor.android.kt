package dev.ayupi.pim.core.data.util

import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ConnectivityNetworkMonitor actual constructor() : NetworkMonitor {
    private val konnectivity = Konnectivity()
    override val isOnline: Flow<Boolean> = konnectivity.isConnectedState
}