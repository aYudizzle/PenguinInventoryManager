package dev.ayupi.pim

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pim.core.ui.theme.PSEAppTheme
import dev.ayupi.pim.app.PSEApp
import dev.ayupi.pim.app.rememberPSEAppState
import dev.ayupi.pim.core.data.util.NetworkMonitor
import dev.ayupi.pim.core.ui.util.toRelativeTime
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun App() {
    val viewModel = koinInject<AppViewModel>()
    val appState = rememberPSEAppState(
        networkMonitor = koinInject<NetworkMonitor>(),
    )
    val uiState by viewModel.userData.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        scope.launch {
            viewModel.refreshData()
        }
    }

    val syncTime = when(uiState) {
        AppUiState.Loading -> "Noch nie"
        is AppUiState.Success -> (uiState as AppUiState.Success).data.lastSyncTimestamp.toRelativeTime()
    }

    LaunchedEffect(Unit) {
        appState.isOnline.collect { isOnline ->
            if(isOnline) {
                viewModel.refreshData()
            }
        }
    }

    PSEAppTheme(
        darkTheme = false
    ) {
        PSEApp(
            appState,
            syncTime = syncTime
        )
    }
}