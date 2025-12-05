package dev.ayupi.pim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.model.UserData
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    userDataRepository: UserDataRepository,
    private val repository: StorageRepository
) : ViewModel() {
    val userData: StateFlow<AppUiState> = userDataRepository.data.map {
        AppUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = AppUiState.Loading,
        started = WhileSubscribed(5_000),
    )

    suspend fun refreshData() {
        repository.refresh()
    }
}

sealed interface AppUiState {
    data object Loading : AppUiState
    data class Success(val data: UserData) : AppUiState
}