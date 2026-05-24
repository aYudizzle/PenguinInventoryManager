package dev.ayupi.pim.feature.itementry

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.model.Item
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.model.StorageItem
import dev.ayupi.pim.core.model.StorageUnit
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ItemEntryViewModel(
    private val itemId: String?,
    private val repository: StorageRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(ItemFormState())
    val uiState = flow {
        emit(ItemEntryUiState.Loading)
        
        val existingItem = itemId?.let {
            repository.getStorageItemById(itemId).firstOrNull()
        }
        val storages = repository.getStorages().first()
        val items = repository.getItems().first()
        val userData = userDataRepository.data.first()

        if (itemId == null && _formState.value.selectedStorageId == null) {
            _formState.update {
                it.copy(selectedStorageId = userData.lastSelectedStorageId ?: storages.firstOrNull()?.id)
            }
        }
        existingItem?.let {
            _formState.update {
                setFormState(existingItem)
            }
        }
        emitAll(
            combine(
                _formState,
                repository.getStorages(),
                repository.getItems()
            ) { form, liveStorages, liveItems ->
                val suggestions = if (form.name.text.isBlank()) {
                    emptyList()
                } else {
                    liveItems.filter { it.name.contains(form.name.text, ignoreCase = true) }
                }

                ItemEntryUiState.Success(
                    form = form,
                    availableStorages = liveStorages,
                    availableItems = liveItems,
                    filteredItems = suggestions
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = ItemEntryUiState.Loading
    )

    private val _events = Channel<ItemFormUiEvent>()
    val events = _events.receiveAsFlow()

    fun onNameChange(name: TextFieldValue) {
        _formState.update { it.copy(name = name) }
    }

    fun onNameSelected(selectedName: String) {
        _formState.update {
            it.copy(
                name = TextFieldValue(
                    text = selectedName,
                    selection = TextRange(selectedName.length)
                )
            )
        }
        viewModelScope.launch {
            val availableItems = repository.getItems().first()
            val existingItem = availableItems.firstOrNull { it.name.equals(selectedName, ignoreCase = true) }
            if (existingItem != null) {
                fillFromExistingItem(existingItem)
            }
        }
    }

    fun onBarcodeChange(barcode: String) {
        val trimmed = barcode.trim()
        _formState.update { it.copy(barcode = trimmed) }
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            val availableItems = repository.getItems().first()
            val existingItem = availableItems.firstOrNull { it.barcode?.trim() == trimmed }
            if (existingItem != null) {
                _formState.update {
                    it.copy(
                        name = TextFieldValue(
                            text = existingItem.name,
                            selection = TextRange(existingItem.name.length)
                        )
                    )
                }
                fillFromExistingItem(existingItem)
            }
        }
    }

    private fun fillFromExistingItem(item: Item) {
        val barcodeVal = item.barcode
        if (_formState.value.barcode.isBlank() && !barcodeVal.isNullOrBlank()) {
            _formState.update { it.copy(barcode = barcodeVal) }
        }

        // Hybride Logik: Erst schauen, ob das Item selbst bereits angepasste Werte hat (> 1 oder nicht g)
        if (item.itemSize > 1 || item.unit != StorageUnit.GRAM) {
            _formState.update {
                it.copy(
                    size = item.itemSize.toString(),
                    unit = item.unit
                )
            }
        } else {
            // Fallback: Datenbank nach dem neuesten StorageItem für dieses Produkt abfragen (auch gelöschte)
            viewModelScope.launch {
                val latestStorageItem = repository.getLatestStorageItemByItemId(item.id)
                if (latestStorageItem != null) {
                    _formState.update {
                        it.copy(
                            size = latestStorageItem.itemSize.toString(),
                            unit = latestStorageItem.unit
                        )
                    }
                } else {
                    // Absoluter Fallback: Die Standardwerte des Items setzen
                    _formState.update {
                        it.copy(
                            size = item.itemSize.toString(),
                            unit = item.unit
                        )
                    }
                }
            }
        }
    }

    fun onQuantityChange(input: String) {
        if (input.all { it.isDigit() }) {
            _formState.update { it.copy(quantity = input) }
        }
    }

    fun onSizeChange(input: String) {
        if (input.all { it.isDigit() }) {
            _formState.update { it.copy(size = input) }
        }
    }

    fun onUnitChange(unit: StorageUnit) {
        _formState.update { it.copy(unit = unit) }
    }

    fun onStorageChange(storageId: String) {
        _formState.update { it.copy(selectedStorageId = storageId) }
        viewModelScope.launch {
            userDataRepository.setLastSelectedStorageId(storageId)
        }
    }

    fun onExpirationDateChange(date: LocalDate?) {
        _formState.update { it.copy(expirationDate = date) }
    }

    fun onSave() {
        val form = _formState.value
        val availableItems = (uiState.value as? ItemEntryUiState.Success)?.availableItems ?: return
        var hasError = false

        val quantity = form.quantity.toLongOrNull()
        if (quantity == null || quantity <= 0) {
            _formState.update {
                it.copy(quantityError = "Bitte gültige Zahl eingeben")
            }
            hasError = true
        }

        if (form.name.text.isBlank() || form.name.text.length > 255) {
            _formState.update {
                it.copy(nameError = "Name muss zwischen 1 und 255 Zeichen lang sein")
            }
            hasError = true
        }
        val size = form.size.toIntOrNull()
        if (size == null || size < 1) {
            _formState.update {
                it.copy(sizeError = "Größe muss mindestens 1 sein")
            }
        }
        if (hasError) {
            _formState.update {
                it.copy(isSaving = false)
            }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            try {
                repository.saveItem(
                    existingInventoryId = form.entryId,
                    itemId = availableItems.firstOrNull { it.name == form.name.text }?.id,
                    itemName = form.name.text,
                    barcode = form.barcode.takeIf { it.isNotBlank() },
                    storageId = form.selectedStorageId!!,
                    quantity = quantity!!,
                    itemSize = size!!,
                    unit = form.unit,
                    expiration = form.expirationDate
                )
                _events.send(ItemFormUiEvent.OnSave)
            } catch(e: Exception) {
                _events.send(ItemFormUiEvent.OnShowSnackbar("Fehler beim Speichern",))
            }
        }

    }


    private fun setFormState(item: StorageItem) =
        ItemFormState(
            entryId = item.id,
            name = TextFieldValue(text=item.item.name),
            barcode = item.item.barcode ?: "",
            quantity = item.quantity.toString(),
            size = item.itemSize.toString(),
            unit = item.unit,
            selectedStorageId = item.storage.id,
        )
}


sealed interface ItemEntryUiState {
    data object Loading : ItemEntryUiState
    data class Success(
        val form: ItemFormState,
        val availableStorages: List<Storage>,
        val availableItems: List<Item>,
        val filteredItems: List<Item> = emptyList(),
    ) : ItemEntryUiState
}

data class ItemFormState(
    val entryId: String? = null, // null = Create Mode
    val name: TextFieldValue = TextFieldValue(), // TextfieldValue - cursor position
    val nameError: String? = null,
    val barcode: String = "",
    val quantity: String = "1",
    val quantityError: String? = null,
    val size: String = "",
    val sizeError: String? = null,
    val unit: StorageUnit = StorageUnit.GRAM,
    val selectedStorageId: String? = null,
    val expirationDate: LocalDate? = null,
    // UI-Status FÜR DAS FORMULAR
    val isSaving: Boolean = false, // Button zeigt Spinner, Formular bleibt da
) {
    val isValid: Boolean
        get() = name.text.isNotBlank() &&
                quantity.toLongOrNull() != null &&
                (size.toIntOrNull() != null) &&
                selectedStorageId != null
}

sealed interface ItemFormUiEvent {
    data object OnSave : ItemFormUiEvent
    data class OnShowSnackbar(val message: String, val actionLabel: String? = null) : ItemFormUiEvent
}