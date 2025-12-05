package dev.ayupi.pim

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.ayupi.pim.di.initKoin
import org.jetbrains.compose.resources.painterResource
import pse_new.composeapp.generated.resources.Res
import pse_new.composeapp.generated.resources.pimlogo

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
        title = "Penguin Inventory Manager",
        state = windowState,
        icon = painterResource(Res.drawable.pimlogo)
    ) {
        App()
    }
}