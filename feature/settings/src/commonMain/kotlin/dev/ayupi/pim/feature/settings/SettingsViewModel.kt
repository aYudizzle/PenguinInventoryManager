package dev.ayupi.pim.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val _expirationDaysInput = MutableStateFlow("")

    val uiState: StateFlow<SettingsUiState> = flow {
        emit(SettingsUiState.Loading)
        val initialUserData = userDataRepository.data.first()
        _expirationDaysInput.value = initialUserData.expirationWarningDays.toString()
        emitAll(
            combine(_expirationDaysInput, userDataRepository.data) { input, userData ->
                SettingsUiState.Success(
                    expirationDaysInput = input,
                    isDarkMode = userData.isDarkMode
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState.Loading
    )

    fun onWarningDaysChange(text: String) {
        val cleanInput = text.filter { it.isDigit() }

        _expirationDaysInput.value = cleanInput
        val days = cleanInput.toIntOrNull()
        days?.let {
            viewModelScope.launch {
                userDataRepository.setExpirationWarningDays(it)
            }
        }
    }

    fun onToggleDarkMode(active: Boolean) {
        viewModelScope.launch {
            userDataRepository.isDarkModeEnabled(active)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val expirationDaysInput: String,
        val isDarkMode: Boolean
    ) : SettingsUiState
}