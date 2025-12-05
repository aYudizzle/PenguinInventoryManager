package dev.ayupi.pim.feature.itementry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pim.core.model.Item
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.model.StorageUnit
import dev.ayupi.pim.core.ui.components.MyDatePickerDialog
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.input.key.* // Für Key Events
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun ItemEntryScreen(
    itemId: String?,
    modifier: Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onNavigateBack: () -> Unit,
) {
    val viewModel: ItemEntryViewModel = koinViewModel<ItemEntryViewModel>(parameters = { parametersOf(itemId) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ItemFormUiEvent.OnSave -> {
                    onNavigateBack()
                }

                is ItemFormUiEvent.OnShowSnackbar -> onShowSnackbar(event.message, event.actionLabel)
            }
        }
    }

    ItemEntryContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNameChange = viewModel::onNameChange,
        onNameSelected = viewModel::onNameSelected,
        onQuantityChange = viewModel::onQuantityChange,
        onUnitChange = viewModel::onUnitChange,
        onSizeChange = viewModel::onSizeChange,
        onStorageChange = viewModel::onStorageChange,
        onExpirationDateChange = viewModel::onExpirationDateChange,
        onSave = viewModel::onSave,
        onShowSnackbar = onShowSnackbar,
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryContent(
    uiState: ItemEntryUiState,
    onNavigateBack: () -> Unit,
    onNameChange: (TextFieldValue) -> Unit,
    onNameSelected: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onUnitChange: (StorageUnit) -> Unit,
    onStorageChange: (String) -> Unit,
    onExpirationDateChange: (LocalDate?) -> Unit,
    onSave: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is ItemEntryUiState.Success && uiState.form.entryId != null) "Produkt bearbeiten" else "Neues Produkt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                ItemEntryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ItemEntryUiState.Success -> {
                    ItemEntryFormContent(
                        form = state.form,
                        storages = state.availableStorages,
                        items = state.filteredItems,
                        onNameChange = onNameChange,
                        onNameSelected = onNameSelected,
                        onQuantityChange = onQuantityChange,
                        onUnitChange = onUnitChange,
                        onSizeChange = onSizeChange,
                        onExpirationDateChange = onExpirationDateChange,
                        onStorageChange = onStorageChange,
                        onSave = onSave,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryFormContent(
    form: ItemFormState,
    items: List<Item>,
    storages: List<Storage>,
    onNameChange: (TextFieldValue) -> Unit,
    onNameSelected: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onUnitChange: (StorageUnit) -> Unit,
    onStorageChange: (String) -> Unit,
    onExpirationDateChange: (LocalDate?) -> Unit,
    onSave: () -> Unit
) {
    var storageDropdownExpanded by remember { mutableStateOf(false) }
    var itemDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var highlightedItemIndex by remember { mutableStateOf(0) }

    LaunchedEffect(items) {
        highlightedItemIndex = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp) // Luft zwischen Elementen
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Produktname", style = MaterialTheme.typography.labelLarge)

            OutlinedTextField(
                value = form.name,
                onValueChange = {
                    onNameChange(it)
                    itemDropdownExpanded = true
                },
                placeholder = { Text("z.B. Haferflocken") },
                modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    if (!itemDropdownExpanded || items.isEmpty()) return@onPreviewKeyEvent false

                    when (event.key) {
                        Key.DirectionDown -> {
                            highlightedItemIndex = (highlightedItemIndex + 1).coerceAtMost(items.lastIndex)
                            true // Event konsumieren
                        }
                        Key.DirectionUp -> {
                            highlightedItemIndex = (highlightedItemIndex - 1).coerceAtLeast(0)
                            true
                        }
                        Key.Enter, Key.Tab -> {
                            if (highlightedItemIndex in items.indices) {
                                val selected = items[highlightedItemIndex]
                                onNameSelected(selected.name)
                                itemDropdownExpanded = false
                                highlightedItemIndex = 0
                                true
                            } else {
                                false // Normales Enter (Next Field) zulassen
                            }
                        }
                        Key.Escape -> {
                            itemDropdownExpanded = false
                            true
                        }
                        else -> false
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { itemDropdownExpanded = true }) {
                        Icon(
                            if (itemDropdownExpanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { itemDropdownExpanded = false }
                ),
                singleLine = true
            )
            DropdownMenu(
                expanded = itemDropdownExpanded,
                onDismissRequest = { itemDropdownExpanded = false },
                properties = PopupProperties(focusable = false)
            ) {
                items.forEachIndexed { index, item ->
                    val isHighlighted = index == highlightedItemIndex
                    DropdownMenuItem(
                        modifier = Modifier.background(
                            if (isHighlighted) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                        ),
                        text = { Text(item.name) },
                        onClick = { onNameSelected(item.name); itemDropdownExpanded = false }
                    )
                }
            }
        }

        // 2. Schrank (Dropdown)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Schrank", style = MaterialTheme.typography.labelLarge)
            val selectedStorageName = storages.find { it.id == form.selectedStorageId }?.name ?: "Schrank auswählen"

            ExposedDropdownMenuBox(
                expanded = storageDropdownExpanded,
                onExpandedChange = { storageDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedStorageName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storageDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = storageDropdownExpanded,
                    onDismissRequest = { storageDropdownExpanded = false }
                ) {
                    storages.forEach { storage ->
                        DropdownMenuItem(
                            text = { Text(storage.name) },
                            onClick = {
                                onStorageChange(storage.id)
                                storageDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically // Damit das "x" mittig sitzt
        ) {
            // 1. ANZAHL (Immer sichtbar)
            OutlinedTextField(
                value = form.quantity,
                onValueChange = onQuantityChange, // Aktualisiert quantityInput
                label = { Text("Anzahl") },
                placeholder = { Text("1") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Logik: Größe nur anzeigen, wenn NICHT "Stück" gewählt ist
            // Kleines "x" dazwischen
            Text("x", style = MaterialTheme.typography.titleMedium)

            // 2. GRÖSSE (Inhalt)
            OutlinedTextField(
                value = form.size,
                onValueChange = onSizeChange, // Neuer Event-Handler!
                label = { Text("Inhalt") },
                placeholder = { Text("500") }, // z.B. Gramm
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column {
                Text("Einheit", style = MaterialTheme.typography.labelLarge)

                // Segmented Button Ersatz (Row mit Chips)
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Höhe an TextField anpassen
                        .padding(4.dp),
                ) {
                    StorageUnit.entries.forEachIndexed { index, unit ->
                        SegmentedButton(
                            selected = unit == form.unit,
                            onClick = { onUnitChange(unit) },
                            label = { Text(text = unit.abbreviation) },
                            shape = SegmentedButtonDefaults.itemShape(index, StorageUnit.entries.size)
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ablaufdatum (Optional)", style = MaterialTheme.typography.labelLarge)

            OutlinedTextField(
                value = form.expirationDate?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Kein Datum") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Datum wählen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    // Der Klick auf das ganze Feld öffnet den Dialog (UX!)
                    .clickable { showDatePicker = true },
                enabled = false, // Trick: Macht es un-fokussierbar, aber Klick oben geht durch Modifier
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        MyDatePickerDialog(
            isOpen = showDatePicker,
            onDismiss = { showDatePicker = false },
            onConfirm = { date -> println(date); onExpirationDateChange(date) },
            initialDate = form.expirationDate
        )

        // 4. Speichern Button
        Button(
            onClick = onSave,
            enabled = form.isValid && !form.isSaving,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E676) // Deine grüne Farbe aus dem Screenshot
            )
        ) {
            if (form.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Speichern", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

}

