package dev.ayupi.pim.feature.itemconsume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.model.StorageItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemConsumeViewModel(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemConsumeUiState>(ItemConsumeUiState.Scanning)
    val uiState = _uiState.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            val activeItems = storageRepository.getStorageItemsByBarcode(barcode)
            if (activeItems.isEmpty()) {
                _uiState.value = ItemConsumeUiState.ProductNotFound(barcode)
            } else if (activeItems.size == 1) {
                _uiState.value = ItemConsumeUiState.ConfirmSingleLocation(activeItems.first())
            } else {
                val productName = activeItems.first().item.name
                _uiState.value = ItemConsumeUiState.SelectLocation(barcode, productName, activeItems)
            }
        }
    }

    fun onSelectLocation(storageItem: StorageItem) {
        _uiState.value = ItemConsumeUiState.ConfirmSingleLocation(storageItem)
    }

    fun onConfirmConsume(storageItem: StorageItem, quantityToRemove: Long) {
        viewModelScope.launch {
            storageRepository.consumeStorageItem(storageItem.id, quantityToRemove)
            val productName = storageItem.item.name
            val storageName = storageItem.storage.name
            _uiState.value = ItemConsumeUiState.Success(
                "Erfolgreich $quantityToRemove x \"$productName\" aus \"$storageName\" entnommen."
            )
            
            // Auto-Reset nach 2 Sekunden zurück zum Scannen
            delay(2000)
            if (_uiState.value is ItemConsumeUiState.Success) {
                resetToScanning()
            }
        }
    }

    fun resetToScanning() {
        _uiState.value = ItemConsumeUiState.Scanning
    }
}

sealed interface ItemConsumeUiState {
    data object Scanning : ItemConsumeUiState
    data class ProductNotFound(val barcode: String) : ItemConsumeUiState
    data class ConfirmSingleLocation(
        val storageItem: StorageItem,
        val quantityToRemove: Long = 1
    ) : ItemConsumeUiState
    data class SelectLocation(
        val barcode: String,
        val productName: String,
        val locations: List<StorageItem>
    ) : ItemConsumeUiState
    data class Success(val message: String) : ItemConsumeUiState
}
