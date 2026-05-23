package dev.ayupi.pim.feature.itementry.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.ayupi.pim.feature.itementry.ItemEntryScreen
import kotlinx.serialization.Serializable

@Serializable
data class ItemEntryRoute(val itemId: String? = null, val triggerScan: Boolean = false)

fun NavController.navigateToItemEntry(itemId: String? = null, triggerScan: Boolean = false, navOptions: NavOptions? = null) =
    navigate(route = ItemEntryRoute(itemId = itemId, triggerScan = triggerScan), navOptions = navOptions)

fun NavGraphBuilder.itemEntryScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onNavigateBack: () -> Unit,
) {
    composable<ItemEntryRoute> {
        val args = it.toRoute<ItemEntryRoute>()
        ItemEntryScreen(
            modifier = modifier,
            itemId = args.itemId,
            triggerScan = args.triggerScan,
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = onNavigateBack,
        )
    }
}