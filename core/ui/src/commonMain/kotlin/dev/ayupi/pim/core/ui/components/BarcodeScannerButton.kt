package dev.ayupi.pim.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BarcodeScannerButton(
    onBarcodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier,
    autoTrigger: Boolean = false
)
