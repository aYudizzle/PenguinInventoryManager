package dev.ayupi.pim.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import dev.ayupi.pim.core.data.util.NetworkMonitor
import dev.ayupi.pim.feature.inventory.navigation.navigateToInventory
import dev.ayupi.pim.feature.itemmaster.navigation.navigateToItemMaster
import dev.ayupi.pim.feature.settings.navigation.navigateToSettings
import dev.ayupi.pim.feature.storagedetails.navigation.navigateToStorageDetails
import dev.ayupi.pim.feature.storageoverview.navigation.navigateToStorageOverview
import dev.ayupi.pim.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberPSEAppState(
    networkMonitor: NetworkMonitor,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    navController,
    coroutineScope,
    networkMonitor
) {
    PSEAppState(
        navController = navController,
        coroutineScope = coroutineScope,
        networkMonitor = networkMonitor
    )
}

class PSEAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigateToNavigationDestination(destination: NavigationDestination) {
        trace("Navigation: ${destination.name}") {
            val destinationOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                }
                launchSingleTop = true
                restoreState = false
            }

            when (destination) {
                NavigationDestination.SETTINGS -> {
                    navController.navigateToSettings(navOptions = destinationOptions)
                }

                NavigationDestination.STORAGE -> {
                    navController.navigateToStorageOverview(navOptions = destinationOptions)
                }

                NavigationDestination.ITEMS -> {
                    navController.navigateToInventory(navOptions = destinationOptions)
                }

                NavigationDestination.DETAILS -> {
                    navController.navigateToStorageDetails("",navOptions = destinationOptions)
                }
                NavigationDestination.MASTER -> {
                    navController.navigateToItemMaster(navOptions = destinationOptions)
                }
            }
        }
    }

    val isOnline = networkMonitor.isOnline.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
}