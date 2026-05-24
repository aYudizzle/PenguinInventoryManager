package dev.ayupi.pim.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun BarcodeScannerButton(
    onBarcodeScanned: (String) -> Unit,
    modifier: Modifier,
    autoTrigger: Boolean
) {
    var showDialog by remember { mutableStateOf(autoTrigger) }
    var barcodeText by remember { mutableStateOf("") }

    IconButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Barcode scannen (Simuliert)",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                barcodeText = ""
            },
            title = {
                Text(text = "Barcode Scanner (Simulation)")
            },
            text = {
                Column {
                    Text(
                        text = "Gib den Barcode manuell ein, um das Scannen auf dem Desktop zu simulieren.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = barcodeText,
                        onValueChange = { barcodeText = it },
                        label = { Text("Barcode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (barcodeText.isNotBlank()) {
                            onBarcodeScanned(barcodeText.trim())
                        }
                        showDialog = false
                        barcodeText = ""
                    }
                ) {
                    Text("Hinzufügen")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        barcodeText = ""
                    }
                ) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
