package dev.ayupi.pse_new.feature.itemmaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pse_new.core.data.repository.StorageRepository
import dev.ayupi.pse_new.core.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemMasterViewModel(
    private val storageRepository: StorageRepository
): ViewModel() {
    val uiState = storageRepository.getItems().map { list ->
        if(list.isEmpty()) ItemMasterUiState.Empty
        else {
            val grouped = list.sortedBy { it.name.lowercase() }
                .groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }

            ItemMasterUiState.Success(
                groupedItems = grouped,
                items = list
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ItemMasterUiState.Loading
    )

    private val _dialogState = MutableStateFlow<EditDialogState?>(null)
    val editDialogState = _dialogState.asStateFlow()

    private val _deleteDialogState = MutableStateFlow<DeleteDialogState?>(null)
    val deleteDialogState = _deleteDialogState.asStateFlow()

    fun onEditClick(item: Item) {
        _dialogState.value = EditDialogState(item.id, item.name)
    }

    fun onDialogDismiss() {
        _dialogState.value = null
    }

    fun onDeleteClick(item: Item) {
        _deleteDialogState.value = DeleteDialogState(item.id, item.name)
    }

    fun onDeleteConfirm() {
        val currentState = _deleteDialogState.value ?: return
        viewModelScope.launch {
            storageRepository.deleteMasterItem(currentState.itemId)
            onDeleteDismiss()
        }
    }
    fun onDeleteDismiss() {
        _deleteDialogState.value = null
    }

    fun onRenameConfirm(newName: String) {
        val current = _dialogState.value ?: return
        if(newName.isBlank() || newName == current.currentName) {
            onDialogDismiss()
            return
        }

        val items = (uiState.value as? ItemMasterUiState.Success)?.items ?: emptyList()
        val isDuplicate = items.any { it.name.equals(newName, ignoreCase = true) && it.id != current.itemId }

        if(isDuplicate) {
            _dialogState.update {
                it?.copy(error = "Produktname mit diesem Name existiert bereits.")
            }
            return
        }
        viewModelScope.launch {
            storageRepository.updateItemName(current.itemId, newName)
            onDialogDismiss()
        }
    }
}

sealed interface ItemMasterUiState {
    data object Loading : ItemMasterUiState
    data class Success(val groupedItems: Map<Char, List<Item>>, val items: List<Item>) : ItemMasterUiState
    data object Empty : ItemMasterUiState
}

data class EditDialogState(
    val itemId: String,
    val currentName: String,
    val error: String? = null
)

data class DeleteDialogState(
    val itemId: String,
    val itemName: String
)