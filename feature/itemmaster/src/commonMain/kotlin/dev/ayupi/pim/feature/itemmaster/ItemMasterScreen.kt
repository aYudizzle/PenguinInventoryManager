package dev.ayupi.pim.feature.itemmaster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pim.core.model.Item
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.ui.text.input.KeyboardType
import dev.ayupi.pim.core.model.StorageUnit

@Composable
fun ItemMasterScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onNavigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<ItemMasterViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val editDialogState by viewModel.editDialogState.collectAsStateWithLifecycle()
    val deleteDialogState by viewModel.deleteDialogState.collectAsStateWithLifecycle()


    ItemMasterContent(
        modifier = modifier,
        state = state,
        editDialogState = editDialogState,
        deleteDialogState = deleteDialogState,
        onDeleteClick = viewModel::onDeleteClick,
        onDeleteDialogDismiss = viewModel::onDeleteDismiss,
        onEditClick = viewModel::onEditClick,
        onEditDialogDismiss = viewModel::onDialogDismiss,
        onEditConfirm = viewModel::onEditConfirm,
        onDeleteConfirm = viewModel::onDeleteConfirm
    )
}

@Composable
fun ItemMasterContent(
    modifier: Modifier,
    state: ItemMasterUiState,
    deleteDialogState: DeleteDialogState?,
    editDialogState: EditDialogState?,
    onEditClick: (Item) -> Unit,
    onEditDialogDismiss: () -> Unit,
    onDeleteClick: (Item) -> Unit,
    onDeleteDialogDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onEditConfirm: (String, String?, Int, StorageUnit) -> Unit,
) {
    when(state) {
        ItemMasterUiState.Empty -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Keine Produkte vorhanden")
            }
        }
        ItemMasterUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is ItemMasterUiState.Success -> {
            LoadedContent(
                modifier = modifier,
                groupedItems = state.groupedItems,
                onEditClick = onEditClick,
                onEditDialogDismiss = onEditDialogDismiss,
                onDeleteClick = onDeleteClick,
                onDeleteDialogDismiss = onDeleteDialogDismiss,
                onDeleteConfirm = onDeleteConfirm,
                dialogState = editDialogState,
                deleteDialogState = deleteDialogState,
                onEditConfirm = onEditConfirm
            )
        }
    }
}

@Composable
fun LoadedContent(
    modifier: Modifier,
    groupedItems: Map<Char, List<Item>>,
    onEditClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit,
    onDeleteDialogDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onEditDialogDismiss: () -> Unit,
    dialogState: EditDialogState?,
    onEditConfirm: (String, String?, Int, StorageUnit) -> Unit,
    deleteDialogState: DeleteDialogState?
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)) {
        groupedItems.forEach { (char, items) ->
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = char.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(items, key = { it.id }) { item ->
                ItemMasterRow(
                    item = item,
                    onEdit = { onEditClick(item) },
                    onDelete = { onDeleteClick(item) }
                )
                HorizontalDivider()
            }
        }
    }
    dialogState?.let { dialogState ->
        EditItemMasterDialog(
            item = dialogState.item,
            onDismiss = onEditDialogDismiss,
            onConfirm = onEditConfirm,
            error = dialogState.error
        )
    }
    deleteDialogState?.let { deleteDialogState ->
        DeleteItemDialog(
            itemName = deleteDialogState.itemName,
            onDismiss = onDeleteDialogDismiss,
            onConfirm = onDeleteConfirm
        )
    }
}

@Composable
fun ItemMasterRow(item: Item, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.itemSize} ${item.unit.abbreviation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!item.barcode.isNullOrBlank()) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Barcode: ${item.barcode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Löschen",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemMasterDialog(
    item: Item,
    onDismiss: () -> Unit,
    error: String? = null,
    onConfirm: (newName: String, newBarcode: String?, newSize: Int, newUnit: StorageUnit) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var barcode by remember { mutableStateOf(item.barcode ?: "") }
    var size by remember { mutableStateOf(item.itemSize.toString()) }
    var unit by remember { mutableStateOf(item.unit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Produkt bearbeiten") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Ändere die Stammdaten des Produkts. Die Änderungen gelten für alle Lagerorte.")

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Produktname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = size,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            size = input
                        }
                    },
                    label = { Text("Packungsgröße") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Einheit", style = MaterialTheme.typography.labelMedium)
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StorageUnit.entries.forEachIndexed { index, storageUnit ->
                            SegmentedButton(
                                selected = storageUnit == unit,
                                onClick = { unit = storageUnit },
                                label = { Text(text = storageUnit.abbreviation) },
                                shape = SegmentedButtonDefaults.itemShape(index, StorageUnit.entries.size)
                            )
                        }
                    }
                }

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalSize = size.toIntOrNull() ?: 1
                    onConfirm(name, barcode.takeIf { it.isNotBlank() }, finalSize, unit)
                },
                enabled = name.isNotBlank() && size.toIntOrNull() != null
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

@Composable
fun DeleteItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var text by remember { mutableStateOf(itemName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Produkt löschen") },
        text = {
            Column {
                Text("Möchtest du '$itemName' wirklich löschen?")
                Spacer(Modifier.height(8.dp))
                Text(
                    "Achtung: Damit werden auch alle Bestände dieses Produkts in allen Schränken entfernt!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() },
                enabled = text.isNotBlank()
            ) {
                Text("Löschen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}