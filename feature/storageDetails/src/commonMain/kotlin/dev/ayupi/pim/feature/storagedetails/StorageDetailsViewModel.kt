package dev.ayupi.pim.feature.storagedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.ui.mapper.toUiModel
import dev.ayupi.pim.core.ui.model.ExpirationStatus
import dev.ayupi.pim.core.ui.model.StorageItemUi
import dev.ayupi.pim.feature.storagedetails.exception.StorageNotFoundException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StorageDetailsViewModel(
    private val storageId: String,
    private val repository: StorageRepository,
    private val userDataRepository: UserDataRepository,
): ViewModel() {
    private val _activeFilter = MutableStateFlow(ItemFilter.ALL)

    val uiState: StateFlow<StorageDetailsUiState> = combine(
        repository.getItemsInStorage(storageId),
        repository.getStorageById(storageId),
        _activeFilter,
        userDataRepository.data
    ) { items, storage, filter, userData ->
        when {
            items.isEmpty() -> StorageDetailsUiState.Empty
            storage == null -> StorageDetailsUiState.Error(StorageNotFoundException("Storage nicht gefunden"))
            else -> {
                val uiItems = items.map { it.toUiModel(userData.expirationWarningDays) }.sortedBy { it.itemName.lowercase() }
                val filteredItems = when(filter){
                    ItemFilter.ALL -> uiItems.groupBy { it.itemName.firstOrNull()?.uppercaseChar() ?: '#' }
                    ItemFilter.EXPIRED_SOON -> uiItems.filter { item ->
                        item.expirationStatus == ExpirationStatus.EXPIRED ||
                                item.expirationStatus == ExpirationStatus.WARNING
                    }.groupBy { it.itemName.firstOrNull()?.uppercaseChar() ?: '#' }
                }
                StorageDetailsUiState.Success(storage = storage, groupedItems = filteredItems, itemFilter = filter)
            }
        }
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StorageDetailsUiState.Loading
        )

    fun onToggleFilter(filter: ItemFilter){
        _activeFilter.update { filter }
    }

    fun onDelete(itemId: String) {
        viewModelScope.launch {
            repository.deleteStorageItem(itemId)
        }
    }
}

sealed interface StorageDetailsUiState {
    data object Loading: StorageDetailsUiState
    data object Empty: StorageDetailsUiState
    data class Success(
        val storage: Storage,
        val groupedItems: Map<Char,List<StorageItemUi>>,
        val itemFilter: ItemFilter,
    ): StorageDetailsUiState
    data class Error(val throwable: Throwable): StorageDetailsUiState
}

enum class ItemFilter {
    ALL,
    EXPIRED_SOON
}