package dev.ayupi.pim.feature.storageoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pim.core.model.StorageSummary
import dev.ayupi.pim.core.ui.util.dashedBorder
import dev.ayupi.pim.core.ui.util.rememberStorageColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StorageOverviewScreen(
    modifier: Modifier = Modifier,
    onStorageSelected: (String) -> Unit,
) {
    val viewModel: StorageOverviewViewModel = koinViewModel<StorageOverviewViewModel>()
    val deleteStorage by viewModel.storageToDelete.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StorageOverviewContent(
        modifier = modifier,
        state = uiState,
        dialogState = dialogState,
        deleteStorage = deleteStorage,
        onStorageSelected = onStorageSelected,
        onEditStorageClick = viewModel::onEditStorageClick,
        onDeleteClick = viewModel::onDeleteStorageClick,
        onAddStorageClick = viewModel::onAddStorageClick,
        onStorageSave = viewModel::onStorageSave,
        onDeleteConfirm = viewModel::onStorageDeleteConfirm,
        onDialogDismiss = viewModel::onDialogDismiss
    )
}

@Composable
fun StorageOverviewContent(
    modifier: Modifier = Modifier,
    state: StorageOverviewUiState,
    dialogState: StorageDialogState?,
    deleteStorage: StorageSummary?,
    onStorageSelected: (String) -> Unit,
    onDeleteClick: (StorageSummary) -> Unit,
    onEditStorageClick: (StorageSummary) -> Unit,
    onAddStorageClick: () -> Unit,
    onStorageSave: (String) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
) {
    when (state) {
        is StorageOverviewUiState.Loading -> Text("Loading...")
        StorageOverviewUiState.Empty -> {}
        is StorageOverviewUiState.Success -> {
            var lastClickTime by remember { mutableLongStateOf(0L) }

            LazyColumn(
                modifier = modifier,
            ) {
                item {
                    AddStorageCard(onClick = onAddStorageClick)
                }
                items(state.storages, key = { it.id }) {
                    StorageCard(
                        storage = it,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTime > 1000) { // 0.5s kann zu kurz sein und einen Doppelklick durchlassen
                                lastClickTime = now
                                onStorageSelected(it.id)
                            }
                        },
                        onEditClick = { onEditStorageClick(it) },
                        onDeleteClick = { onDeleteClick(it) }
                    )
                }
            }
            deleteStorage?.let {
                AlertDialog(
                    onDismissRequest = onDialogDismiss,
                    title = { Text("Lager löschen?") },
                    text = {
                        Column {
                            Text("'${it.name}' wirklich löschen?")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Achtung: Alle ${it.productCount} Produkte darin werden ebenfalls gelöscht!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onDeleteConfirm,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Löschen")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDialogDismiss) { Text("Abbrechen") }
                    }
                )
            }
            dialogState?.let {
                AddEditStorageDialog(
                    initialName = it.initialStorageName ?: "",
                    onDismiss = onDialogDismiss,
                    error = it.error,
                    onConfirm = onStorageSave
                )
            }

        }
    }

}


@Composable
fun StorageCard(
    storage: StorageSummary,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val (iconBgColor, iconTintColor) = rememberStorageColors(storage.name)
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = storage.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val productText = if (storage.productCount == 1) "1 Produkt" else "${storage.productCount} Produkte"

                Text(
                    text = productText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Optionen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // Option A: Öffnen (Optional, da Klick auf Karte das auch macht)
                    DropdownMenuItem(
                        text = { Text("Öffnen") },
                        onClick = {
                            showMenu = false
                            onClick()
                        },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )

                    DropdownMenuItem(
                        text = { Text("Umbenennen") },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Löschen", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddStorageCard(
    onClick: () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val contentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .dashedBorder(
                width = 2.dp,
                color = borderColor,
                cornerRadius = 16.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            )
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "Neuen Schrank hinzufügen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}


@Composable
fun AddEditStorageDialog(
    initialName: String,
    onDismiss: () -> Unit,
    error: String? = null,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lager hinzufügen/editieren") },
        text = {
            Column {
                Text("Ändere den Namen den Lagerort.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { event ->
                        if(event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                            onConfirm(text)
                            true
                        } else false
                    },
                    supportingText = {
                        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}