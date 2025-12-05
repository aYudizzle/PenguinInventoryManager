package dev.ayupi.pim.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.ayupi.pim.feature.itementry.navigation.navigateToItemEntry
import dev.ayupi.pim.navigation.NavigationDestination
import dev.ayupi.pim.navigation.PSENavHost
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun PSEApp(
    appState: PSEAppState,
    syncTime: String,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = NavigationDestination.entries.find { destination ->
        currentDestination.isRouteInHierarchy(destination.route)
    }
    val showFab =
        currentTopLevelDestination == NavigationDestination.ITEMS || currentTopLevelDestination == NavigationDestination.STORAGE || currentTopLevelDestination == NavigationDestination.DETAILS

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        appState.navController.navigateToItemEntry()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Eintrag hinzufügen"
                    )
                }
            }
        },
        topBar = {
            currentTopLevelDestination?.let {
                TopAppBar(
                    navigationIcon = {
                        if(it == NavigationDestination.DETAILS || it == NavigationDestination.MASTER) {
                            IconButton(onClick = { appState.navController.navigateUp() }) {
                                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, null)
                            }
                        }
                    },
                    title = {
                        Text(stringResource(it.titleTextResource))
                },
                    actions = {
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = "Lezte Sync: $syncTime",
                            fontSize = 16.sp
                        )
                    }
                )
            }
        },
        bottomBar = {
            BottomAppBar {
                NavigationDestination.entries.forEach { destination ->
                    if (destination.bottomNav) {
                        val selected = currentDestination.isRouteInHierarchy(destination.route)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { appState.navigateToNavigationDestination(destination) },
                            icon = {
                                val icon = if (selected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = stringResource(destination.iconTestResource),
                                )
                            },
                            label = {},
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        PSENavHost(
            appState = appState,
            paddingValues = paddingValues,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short
                ) == SnackbarResult.ActionPerformed
            }

        )
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false