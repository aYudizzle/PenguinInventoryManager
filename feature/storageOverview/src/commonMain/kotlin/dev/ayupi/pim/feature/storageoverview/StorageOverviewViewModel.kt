package dev.ayupi.pim.feature.storageoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.model.StorageSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StorageOverviewViewModel(
    private val storageRepository: StorageRepository
): ViewModel() {
    val uiState: StateFlow<StorageOverviewUiState> = storageRepository.getStorageSummaries().map { summaries ->
        if (summaries.isEmpty()) StorageOverviewUiState.Empty
        else StorageOverviewUiState.Success(summaries)
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5_000),
        StorageOverviewUiState.Loading
    )

    private val _dialogState = MutableStateFlow<StorageDialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    private val _storageToDelete = MutableStateFlow<StorageSummary?>(null)
    val storageToDelete = _storageToDelete.asStateFlow()

    fun onAddStorageClick() {
        _dialogState.value = StorageDialogState()
    }

    fun onEditStorageClick(storage: StorageSummary) {
        _dialogState.value = StorageDialogState(
            storageId = storage.id,
            initialStorageName = storage.name
        )
    }

    fun onDeleteStorageClick(storage: StorageSummary) {
        _storageToDelete.value = storage
    }

    fun onDialogDismiss() {
        _dialogState.value = null
        _storageToDelete.value = null
    }

    fun onStorageSave(storageName: String) {
        val current = _dialogState.value ?: return

        if(storageName.isBlank() || storageName == current.initialStorageName) {
            onDialogDismiss()
            return
        }

        val items = (uiState.value as? StorageOverviewUiState.Success)?.storages ?: emptyList()
        val isDuplicate = items.any { it.name.equals(storageName, ignoreCase = true) && it.id != current.storageId }

        if(isDuplicate) {
            _dialogState.update {
                it?.copy(error = "Ein Lager mit diesem Namen existiert bereits.")
            }
            return
        }
        onDialogDismiss()
        viewModelScope.launch {
            current.storageId?.let {
                storageRepository.updateStorageName(
                    newName = storageName,
                    id = it
                )
            } ?: storageRepository.addStorage(storageName)
        }
    }

    fun onStorageDeleteConfirm() {
        val storage = _storageToDelete.value ?: return
        onDialogDismiss()
        viewModelScope.launch {
            storageRepository.deleteStorage(storage.id)
        }
    }
}

sealed interface StorageOverviewUiState {
    data object Loading : StorageOverviewUiState
    data class Success(val storages: List<StorageSummary>) : StorageOverviewUiState
    data object Empty : StorageOverviewUiState
}

data class StorageDialogState(
    val storageId: String? = null,
    val initialStorageName: String? = null,
    val error: String? = null
)