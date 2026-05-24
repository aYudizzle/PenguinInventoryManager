package dev.ayupi.pim.feature.itemconsume

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ItemConsumeRoute

fun NavController.navigateToItemConsume(navOptions: NavOptions? = null) =
    navigate(route = ItemConsumeRoute, navOptions = navOptions)

fun NavGraphBuilder.itemConsumeScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    composable<ItemConsumeRoute> {
        ItemConsumeScreen(
            modifier = modifier,
            onNavigateBack = onNavigateBack
        )
    }
}
