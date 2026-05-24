package dev.ayupi.pim.feature.itemrelocate

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ItemRelocateRoute

fun NavController.navigateToItemRelocate(navOptions: NavOptions? = null) =
    navigate(route = ItemRelocateRoute, navOptions = navOptions)

fun NavGraphBuilder.itemRelocateScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    composable<ItemRelocateRoute> {
        ItemRelocateScreen(
            modifier = modifier,
            onNavigateBack = onNavigateBack
        )
    }
}
