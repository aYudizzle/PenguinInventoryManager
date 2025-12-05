package dev.ayupi.pim.feature.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.ui.mapper.toUiModel
import dev.ayupi.pim.core.ui.model.ExpirationStatus
import dev.ayupi.pim.core.ui.model.StorageItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class InventoryViewModel(
    private val storageRepository: StorageRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStorageId = MutableStateFlow<String?>(null)

    private val _showOnlyWarnings = MutableStateFlow(false)

    private val _filterFlow = combine(
        _searchQuery,
        _selectedStorageId,
        _showOnlyWarnings
    ) { query, storageId, showWarnings ->
        InventoryFilters(query, storageId, showWarnings)
    }

    val uiState = combine(
        storageRepository.getInventory(),
        storageRepository.getStorages(),
        userDataRepository.data,
        _filterFlow,
    ) { allItems, storages, userData, filters ->
        var filtered = allItems
        filters.storageId?.let { storageId ->
            filtered = filtered.filter { it.storage.id == storageId }
        }
        if(filters.query.isNotBlank()) {
            filtered = filtered.filter {
                it.item.name.contains(filters.query, ignoreCase = true)
            }
        }


        val uiModel = filtered.map { it.toUiModel(userData.expirationWarningDays) }
            .sortedBy { it.itemName.lowercase() }
            .let {
                if(filters.showOnlyWarnings) {
                    it.filter { item ->
                        item.expirationStatus == ExpirationStatus.EXPIRED ||
                                item.expirationStatus == ExpirationStatus.WARNING
                    }
                } else it
            }
        InventoryUiState.Success(
            items = uiModel,
            availableStorages = storages,
            selectedStorageId = filters.storageId,
            searchQuery = filters.query,
            showOnlyWarnings = filters.showOnlyWarnings
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = InventoryUiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStorageFilterSelected(storageId: String?) {
        if (_selectedStorageId.value == storageId) {
            _selectedStorageId.value = null
        } else {
            _selectedStorageId.value = storageId
        }
    }

    fun onToggleWarningFilter(active: Boolean) {
        _showOnlyWarnings.value = active
    }
}

sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data class Success(
        val items: List<StorageItemUi>,
        val availableStorages: List<Storage>,
        val selectedStorageId: String?,
        val searchQuery: String,
        val showOnlyWarnings: Boolean = false,
    ) : InventoryUiState
}

private data class InventoryFilters(
    val query: String,
    val storageId: String?,
    val showOnlyWarnings: Boolean
)