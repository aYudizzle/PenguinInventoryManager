package dev.ayupi.pse_new.feature.settings.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.ayupi.pse_new.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions = navOptions)

fun NavGraphBuilder.settingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToItemMaster: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(
            modifier = modifier,
            onNavigateToItemMaster = onNavigateToItemMaster
        )
    }
}