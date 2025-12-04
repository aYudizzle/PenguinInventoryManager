package dev.ayupi.pse_new

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.ayupi.pse_new.di.initKoin

fun main() = application {
    initKoin()
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        isMinimized = false,
        position = WindowPosition.PlatformDefault,
        size = DpSize(1280.dp, 960.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "pse_new",
        state = windowState,
    ) {
        App()
    }
}