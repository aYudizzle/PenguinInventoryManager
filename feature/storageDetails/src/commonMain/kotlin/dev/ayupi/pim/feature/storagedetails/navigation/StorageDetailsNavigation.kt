package dev.ayupi.pim.feature.storagedetails.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.ayupi.pim.feature.storagedetails.StorageDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
data class StorageDetailsRoute(val storageId: String)

fun NavController.navigateToStorageDetails(storageId: String, navOptions: NavOptions? = null) =
    navigate(route = StorageDetailsRoute(storageId = storageId), navOptions = navOptions)

fun NavGraphBuilder.storageDetailsScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onItemClicked: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<StorageDetailsRoute> {
        val args = it.toRoute<StorageDetailsRoute>()
        StorageDetailsScreen(
            modifier = modifier,
            storageId = args.storageId,
            onShowSnackbar = onShowSnackbar,
            onNavigateBack = onNavigateBack,
            onItemClicked = onItemClicked
        )
    }
}