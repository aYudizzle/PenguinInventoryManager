package dev.ayupi.pse_new.feature.inventory.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.ayupi.pse_new.feature.inventory.InventoryScreen
import kotlinx.serialization.Serializable

@Serializable
data object InventoryRoute

fun NavController.navigateToInventory(navOptions: NavOptions? = null) =
    navigate(route = InventoryRoute, navOptions = navOptions)

fun NavGraphBuilder.inventoryScreen(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
) {
    composable<InventoryRoute> {
        InventoryScreen(
            modifier = modifier,
            onItemClick = onItemClick
        )
    }
}