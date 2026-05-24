package dev.ayupi.pim.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.ayupi.pim.feature.itementry.navigation.navigateToItemEntry
import dev.ayupi.pim.feature.itemconsume.navigateToItemConsume
import dev.ayupi.pim.feature.itemrelocate.navigateToItemRelocate
import dev.ayupi.pim.navigation.NavigationDestination
import dev.ayupi.pim.navigation.PSENavHost
import dev.ayupi.pim.core.ui.components.SpeedDialFab
import dev.ayupi.pim.core.ui.components.SpeedDialItem
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

    var isFabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentDestination) {
        isFabExpanded = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

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
        Box(modifier = Modifier.fillMaxSize()) {
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

            if (isFabExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isFabExpanded = false
                        }
                )
            }

            if (showFab) {
                SpeedDialFab(
                    isExpanded = isFabExpanded,
                    onExpandChanged = { isFabExpanded = it },
                    items = listOf(
                        SpeedDialItem(
                            icon = Icons.Default.Remove,
                            label = "Bestand entnehmen",
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = {
                                appState.navController.navigateToItemConsume()
                            }
                        ),
                        SpeedDialItem(
                            icon = Icons.Default.SwapHoriz,
                            label = "Bestand umlagern",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = {
                                appState.navController.navigateToItemRelocate()
                            }
                        ),
                        SpeedDialItem(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Mit Barcode eintragen",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = {
                                appState.navController.navigateToItemEntry(triggerScan = true)
                            }
                        ),
                        SpeedDialItem(
                            icon = Icons.Default.Add,
                            label = "Manuell eintragen",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = {
                                appState.navController.navigateToItemEntry()
                            }
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = paddingValues.calculateBottomPadding() + 16.dp,
                            end = 16.dp
                        )
                )
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false