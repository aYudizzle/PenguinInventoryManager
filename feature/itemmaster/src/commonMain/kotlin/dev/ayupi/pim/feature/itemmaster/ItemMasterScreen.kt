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
        onRenameConfirm = viewModel::onRenameConfirm,
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
    onRenameConfirm: (String) -> Unit,
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
                onRenameConfirm = onRenameConfirm
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
    onRenameConfirm: (String) -> Unit,
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
        RenameItemDialog(
            initialName = dialogState.currentName,
            onDismiss = onEditDialogDismiss,
            onConfirm = onRenameConfirm,
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
            .clickable { onEdit() } // Klick auf Zeile öffnet auch Edit? Oder nur Button?
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Löschen",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f) // Leicht abgeschwächt
            )
        }
    }
}

@Composable
fun RenameItemDialog(
    initialName: String,
    onDismiss: () -> Unit,
    error: String? = null,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Produkt umbenennen") },
        text = {
            Column {
                Text("Ändere den Namen für alle Lagerorte.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
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

@Composable
fun DeleteItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var text by remember { mutableStateOf(itemName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Produkt umbenennen") },
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