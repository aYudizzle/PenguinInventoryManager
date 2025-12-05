package dev.ayupi.pim.feature.storageoverview.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.ayupi.pim.feature.storageoverview.StorageOverviewScreen
import kotlinx.serialization.Serializable

@Serializable
data object StorageOverviewRoute

fun NavController.navigateToStorageOverview(navOptions: NavOptions? = null) =
    navigate(route = StorageOverviewRoute, navOptions = navOptions)

fun NavGraphBuilder.storageOverviewScreen(
    modifier: Modifier = Modifier,
    onStorageSelected: (storageId: String) -> Unit,
) {
    composable<StorageOverviewRoute> {
        StorageOverviewScreen(
            modifier = modifier,
            onStorageSelected = onStorageSelected,
        )
    }
}