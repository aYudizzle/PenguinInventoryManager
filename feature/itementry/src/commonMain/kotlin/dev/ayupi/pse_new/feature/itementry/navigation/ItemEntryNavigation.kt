package dev.ayupi.pse_new.feature.itementry.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.ayupi.pse_new.feature.itementry.ItemEntryScreen
import kotlinx.serialization.Serializable

@Serializable
data class ItemEntryRoute(val itemId: String?)

fun NavController.navigateToItemEntry(itemId: String? = null, navOptions: NavOptions? = null) =
    navigate(route = ItemEntryRoute(itemId = itemId), navOptions = navOptions)

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
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = onNavigateBack,
        )
    }
}