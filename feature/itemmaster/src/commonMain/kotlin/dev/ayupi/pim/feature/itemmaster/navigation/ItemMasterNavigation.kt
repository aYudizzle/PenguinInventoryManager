package dev.ayupi.pim.feature.itemmaster.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.ayupi.pim.feature.itemmaster.ItemMasterScreen
import kotlinx.serialization.Serializable

@Serializable
data object ItemMasterRoute

fun NavController.navigateToItemMaster(navOptions: NavOptions? = null) =
    navigate(route = ItemMasterRoute, navOptions = navOptions)

fun NavGraphBuilder.itemMasterScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onNavigateBack: () -> Unit,
) {
    composable<ItemMasterRoute> {
        ItemMasterScreen(
            modifier = modifier,
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = onNavigateBack,
        )
    }
}