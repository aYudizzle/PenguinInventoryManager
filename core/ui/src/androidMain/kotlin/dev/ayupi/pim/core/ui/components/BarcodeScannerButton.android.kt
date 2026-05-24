package dev.ayupi.pim.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
actual fun BarcodeScannerButton(
    onBarcodeScanned: (String) -> Unit,
    modifier: Modifier,
    autoTrigger: Boolean
) {
    val context = LocalContext.current
    val scanner = remember(context) { GmsBarcodeScanning.getClient(context) }

    val startScan = {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { onBarcodeScanned(it) }
            }
            .addOnFailureListener { e ->
                // Failure can be logged or ignored
            }
    }

    if (autoTrigger) {
        LaunchedEffect(Unit) {
            startScan()
        }
    }

    IconButton(
        onClick = { startScan() },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Barcode scannen",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
