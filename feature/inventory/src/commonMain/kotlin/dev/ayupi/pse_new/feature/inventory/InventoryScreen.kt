package dev.ayupi.pse_new.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import dev.ayupi.pse_new.core.model.Storage
import dev.ayupi.pse_new.core.ui.components.StorageChip
import dev.ayupi.pse_new.core.ui.components.StorageItemRow
import dev.ayupi.pse_new.core.ui.model.StorageItemUi
import dev.ayupi.pse_new.core.ui.util.rememberStorageColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InventoryScreen(
    modifier: Modifier,
    onItemClick: (String) -> Unit,
) {
    val viewModel: InventoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InventoryContent(
        modifier = modifier,
        state = uiState,
        onItemClick = onItemClick,
        onStorageFilterSelected = viewModel::onStorageFilterSelected,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleWarnings = viewModel::onToggleWarningFilter
    )
}

@Composable
fun InventoryContent(
    modifier: Modifier,
    state: InventoryUiState,
    onStorageFilterSelected: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onToggleWarnings: (Boolean) -> Unit
) {
    when (state) {
        InventoryUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is InventoryUiState.Success -> {
            LoadedContent(
                modifier = modifier,
                storages = state.availableStorages,
                items = state.items,
                searchQuery = state.searchQuery,
                selectedStorageId = state.selectedStorageId,
                showOnlyWarnings = state.showOnlyWarnings,
                onStorageFilterSelected = onStorageFilterSelected,
                onSearchQueryChange = onSearchQueryChange,
                onItemClick = onItemClick,
                onToggleWarnings = onToggleWarnings
            )
        }
    }
}

@Composable
fun LoadedContent(
    modifier: Modifier,
    storages: List<Storage>,
    items: List<StorageItemUi>,
    searchQuery: String,
    showOnlyWarnings: Boolean,
    selectedStorageId: String?,
    onStorageFilterSelected: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onToggleWarnings: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        InventorySearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange
        )
        StorageFilterRow(
            storages = storages,
            selectedId = selectedStorageId,
            onSelect = onStorageFilterSelected,
            showOnlyWarnings = showOnlyWarnings,
            onToggleWarnings = onToggleWarnings
        )
        if (items.isEmpty()) {
            Text("Keine Produkte gefunden", color = Color.Gray)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // Platz für FAB
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    StorageItemRow(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun InventorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Produkte suchen...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, "Suche löschen")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                    if (query.isNotEmpty()) {
                        onQueryChange("")
                        true
                    } else false
                } else false
            }
    )
}

@Composable
fun StorageFilterRow(
    storages: List<Storage>,
    selectedId: String?,
    showOnlyWarnings: Boolean,
    onToggleWarnings: (Boolean) -> Unit,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            ShowWarningToggleChip(
                isSelected = showOnlyWarnings,
                onClick = { onToggleWarnings(!showOnlyWarnings) },
                label = "Läuft ab",
                leadingIcon = {
                    if (showOnlyWarnings) {
                        Icon(Icons.Default.Check, null)
                    } else {
                        Icon(Icons.Outlined.Warning, null) // Visueller Hinweis
                    }
                },
            )
        }
        item { VerticalDivider(modifier = Modifier.height(24.dp)) }
        item {
            StorageChip(
                label = "Alle",
                isSelected = selectedId == null,
                onClick = { onSelect(null) },
            )
        }

        items(storages) { storage ->
            StorageChip(
                label = storage.name,
                isSelected = selectedId == storage.id,
                onClick = { onSelect(storage.id) },
            )
        }
    }
}

@Composable
fun ShowWarningToggleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    useDynamicColor: Boolean = false,
) {
    val (baseBg, baseContent) = if(useDynamicColor) rememberStorageColors(label)
    else MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer

    // Wenn ausgewählt: Volle Farbe. Wenn nicht: Ausgegraut/Transparent oder blass.
    val backgroundColor = if (isSelected) baseBg else Color.Transparent
    val contentColor = if (isSelected) baseContent else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) baseBg else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon()
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            trailingIcon()
        }
    }
}