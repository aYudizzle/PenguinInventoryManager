package dev.ayupi.pim.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import dev.ayupi.pim.feature.inventory.navigation.InventoryRoute
import dev.ayupi.pim.feature.itemmaster.navigation.ItemMasterRoute
import dev.ayupi.pim.feature.settings.navigation.SettingsRoute
import dev.ayupi.pim.feature.storagedetails.navigation.StorageDetailsRoute
import dev.ayupi.pim.feature.storageoverview.navigation.StorageOverviewRoute
import org.jetbrains.compose.resources.StringResource
import pse_new.composeapp.generated.resources.Res
import pse_new.composeapp.generated.resources.navigation_item_master
import pse_new.composeapp.generated.resources.navigation_items
import pse_new.composeapp.generated.resources.navigation_settings
import pse_new.composeapp.generated.resources.navigation_storage_details
import pse_new.composeapp.generated.resources.navigation_storage_overview
import kotlin.reflect.KClass

enum class NavigationDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTestResource: StringResource,
    val titleTextResource: StringResource,
    val bottomNav: Boolean = true,
    val route: KClass<*>,
) {
    STORAGE(
        selectedIcon = Icons.Filled.Storage,
        unselectedIcon = Icons.Outlined.Storage,
        iconTestResource = Res.string.navigation_storage_overview,
        titleTextResource = Res.string.navigation_storage_overview,
        route = StorageOverviewRoute::class,
    ),
    ITEMS(
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        iconTestResource = Res.string.navigation_items,
        titleTextResource = Res.string.navigation_items,
        route = InventoryRoute::class,
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        iconTestResource = Res.string.navigation_settings,
        titleTextResource = Res.string.navigation_settings,
        route = SettingsRoute::class,
    ),
    DETAILS(
        selectedIcon = Icons.Filled.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox,
        iconTestResource = Res.string.navigation_storage_details,
        titleTextResource = Res.string.navigation_storage_details,
        bottomNav = false,
        route = StorageDetailsRoute::class,
    ),
    MASTER(
        selectedIcon = Icons.Filled.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox,
        iconTestResource = Res.string.navigation_item_master,
        titleTextResource = Res.string.navigation_item_master,
        bottomNav = false,
        route = ItemMasterRoute::class,
    )
}