package dev.ayupi.pse_new.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import dev.ayupi.pse_new.app.PSEAppState
import dev.ayupi.pse_new.feature.inventory.navigation.InventoryRoute
import dev.ayupi.pse_new.feature.inventory.navigation.inventoryScreen
import dev.ayupi.pse_new.feature.itementry.navigation.itemEntryScreen
import dev.ayupi.pse_new.feature.itementry.navigation.navigateToItemEntry
import dev.ayupi.pse_new.feature.itemmaster.navigation.itemMasterScreen
import dev.ayupi.pse_new.feature.itemmaster.navigation.navigateToItemMaster
import dev.ayupi.pse_new.feature.settings.navigation.settingsScreen
import dev.ayupi.pse_new.feature.storagedetails.navigation.navigateToStorageDetails
import dev.ayupi.pse_new.feature.storagedetails.navigation.storageDetailsScreen
import dev.ayupi.pse_new.feature.storageoverview.navigation.storageOverviewScreen

@Composable
fun PSENavHost(
    appState: PSEAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = InventoryRoute,
        modifier = modifier,
    ) {
        storageOverviewScreen(
            modifier = Modifier.padding(paddingValues),
            onStorageSelected = { navController.navigateToStorageDetails(it) }
        )
        storageDetailsScreen(
            modifier = Modifier.padding(paddingValues),
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = navController::navigateUp,
            onItemClicked = { navController.navigateToItemEntry(it) }
        )
        itemEntryScreen(
            modifier = Modifier.padding(paddingValues),
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = navController::navigateUp,
        )
        settingsScreen(
            modifier = Modifier.padding(paddingValues),
            onNavigateToItemMaster = { navController.navigateToItemMaster() },
        )
        itemMasterScreen(
            modifier = Modifier.padding(paddingValues),
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = navController::navigateUp,
        )
        inventoryScreen(
            modifier = Modifier.padding(paddingValues),
            onItemClick = { navController.navigateToItemEntry(it) }
        )
    }
}