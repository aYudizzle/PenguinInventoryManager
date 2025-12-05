package dev.ayupi.pim.feature.storagedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.ui.components.StorageItemRow
import dev.ayupi.pim.core.ui.model.StorageItemUi
import dev.ayupi.pim.feature.storagedetails.util.calculateHeaderIndices
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StorageDetailsScreen(
    modifier: Modifier,
    storageId: String,
    onNavigateBack: () -> Unit,
    onItemClicked: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val viewModel: StorageDetailsViewModel = koinViewModel<StorageDetailsViewModel> {
        parametersOf(storageId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StorageDetailsContent(
        modifier = modifier,
        state = uiState,
        onNavigateBack = onNavigateBack,
        onShowSnackbar = onShowSnackbar,
        onItemClicked = onItemClicked,
        onDelete = viewModel::onDelete,
        onToggleFilter = viewModel::onToggleFilter
    )
}

@Composable
fun StorageDetailsContent(
    modifier: Modifier,
    state: StorageDetailsUiState,
    onNavigateBack: () -> Unit,
    onToggleFilter: (filter: ItemFilter) -> Unit,
    onDelete: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onItemClicked: (String) -> Unit
) {
    when (state) {
        is StorageDetailsUiState.Error -> {}
        StorageDetailsUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is StorageDetailsUiState.Success -> {
            LoadedInventoryContent(
                modifier = modifier,
                storage = state.storage,
                groupedItems = state.groupedItems,
                onItemClicked = onItemClicked,
                onNavigateBack = onNavigateBack,
                onToggleFilter = onToggleFilter,
                onDelete = onDelete,
                itemFilter = state.itemFilter
            )
        }

        StorageDetailsUiState.Empty -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Keine Produkte vorhanden")
                TextButton(onClick = onNavigateBack) {
                    Text("Zurück zur Übersicht")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadedInventoryContent(
    storage: Storage,
    groupedItems: Map<Char, List<StorageItemUi>>,
    itemFilter: ItemFilter,
    onItemClicked: (String) -> Unit,
    onToggleFilter: (filter: ItemFilter) -> Unit,
    onDelete: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val headerIndices = remember(groupedItems) {
        calculateHeaderIndices(groupedItems)
    }

    val availableLetters = remember(groupedItems) {
        groupedItems.keys.toList()
    }

    var itemToDelete by remember { mutableStateOf<StorageItemUi?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            AlphabetFilterBar(
                itemFilter = itemFilter,
                onToggleFilter = onToggleFilter,
                availableLetters = availableLetters,
                onLetterClick = { letter ->
                    val indexToScroll = headerIndices[letter] ?: 0
                    scope.launch { listState.animateScrollToItem(indexToScroll) }
                }
            )
            if (groupedItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) { Text("Keine Produkte im Filter vorhanden, welche abgelaufen bzw. kurz davor sind.") }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp, start = 16.dp, end = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    state = listState
                ) {
                    groupedItems.forEach { (initial, itemList) ->
                        stickyHeader {
                            InitialListHeader(initial.toString())
                        }
                        items(itemList, key = { it.id }) { item ->
                            StorageItemSwipeBox(
                                onDelete = { itemToDelete = item },
                                content = {
                                    StorageItemRow(
                                        item = item,
                                        onClick = { onItemClicked(item.id) }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        ListScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 4.dp),
            state = listState
        )
        itemToDelete?.let {
            DeleteDialog(
                onDismiss = {
                    itemToDelete = null
                },
                itemToDelete = it,
                onDelete = {
                    onDelete(it.id)
                    itemToDelete = null
                }
            )
        }
    }
}

@Composable
fun InitialListHeader(initial: String) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    itemToDelete: StorageItemUi,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${itemToDelete.itemName} löschen?") },
        text = { Text("Möchtest du ${itemToDelete.itemName} wirklich löschen?") },
        confirmButton = {
            TextButton(onClick = { onDelete() }) { Text("Löschen") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}

@Composable
fun StorageItemSwipeBox(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.25f } // TODO: actually not working? Bugged? try to implement scale rotation etc. again, after threshold is fixed
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, RoundedCornerShape(18.dp))
                    .padding(horizontal = 26.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                          Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Löschen",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
            }
        },
        modifier = modifier,
        content = { content() }
    )
}

@Composable
expect fun AlphabetFilterBar(
    itemFilter: ItemFilter,
    onToggleFilter: (filter: ItemFilter) -> Unit,
    availableLetters: List<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
)

@Composable
expect fun ListScrollbar(
    modifier: Modifier,
    state: LazyListState,
)