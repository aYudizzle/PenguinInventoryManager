package dev.ayupi.pim.feature.itemrelocate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.model.StorageItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ItemRelocateViewModel(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemRelocateUiState>(ItemRelocateUiState.Scanning)
    val uiState = _uiState.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            val activeItems = storageRepository.getStorageItemsByBarcode(barcode)
            if (activeItems.isEmpty()) {
                _uiState.value = ItemRelocateUiState.ProductNotFound(barcode)
            } else if (activeItems.size == 1) {
                onSourceSelected(activeItems.first())
            } else {
                val productName = activeItems.first().item.name
                _uiState.value = ItemRelocateUiState.SelectSource(barcode, productName, activeItems)
            }
        }
    }

    fun onSourceSelected(sourceStorageItem: StorageItem) {
        viewModelScope.launch {
            val allStorages = storageRepository.getStorages().first()
            val available = allStorages.filter { it.id != sourceStorageItem.storage.id }
            _uiState.value = ItemRelocateUiState.SelectTarget(sourceStorageItem, available)
        }
    }

    fun onTargetSelected(sourceStorageItem: StorageItem, targetStorage: Storage) {
        _uiState.value = ItemRelocateUiState.Confirm(sourceStorageItem, targetStorage, 1L)
    }

    fun onConfirmRelocate(sourceStorageItem: StorageItem, targetStorage: Storage, quantityToMove: Long) {
        viewModelScope.launch {
            storageRepository.relocateStorageItem(sourceStorageItem.id, targetStorage.id, quantityToMove)
            val productName = sourceStorageItem.item.name
            val sourceName = sourceStorageItem.storage.name
            val targetName = targetStorage.name
            _uiState.value = ItemRelocateUiState.Success(
                "Erfolgreich $quantityToMove x \"$productName\" von \"$sourceName\" nach \"$targetName\" verlegt."
            )

            delay(2000)
            if (_uiState.value is ItemRelocateUiState.Success) {
                resetToScanning()
            }
        }
    }

    fun resetToScanning() {
        _uiState.value = ItemRelocateUiState.Scanning
    }
}

sealed interface ItemRelocateUiState {
    data object Scanning : ItemRelocateUiState
    data class ProductNotFound(val barcode: String) : ItemRelocateUiState
    data class SelectSource(
        val barcode: String,
        val productName: String,
        val locations: List<StorageItem>
    ) : ItemRelocateUiState
    data class SelectTarget(
        val sourceStorageItem: StorageItem,
        val availableStorages: List<Storage>
    ) : ItemRelocateUiState
    data class Confirm(
        val sourceStorageItem: StorageItem,
        val targetStorage: Storage,
        val quantityToMove: Long = 1
    ) : ItemRelocateUiState
    data class Success(val message: String) : ItemRelocateUiState
}
