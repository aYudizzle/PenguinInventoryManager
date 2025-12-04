package dev.ayupi.pse_new.feature.storageoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pse_new.core.data.repository.StorageRepository
import dev.ayupi.pse_new.core.model.Storage
import dev.ayupi.pse_new.core.model.StorageItem
import dev.ayupi.pse_new.core.model.StorageSummary
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
}

sealed interface StorageOverviewUiState {
    data object Loading : StorageOverviewUiState
    data class Success(val storages: List<StorageSummary>) : StorageOverviewUiState
    data object Empty : StorageOverviewUiState
}