package dev.ayupi.pse_new.core.data.repository

import dev.ayupi.pse_new.core.model.Item
import dev.ayupi.pse_new.core.model.Storage
import dev.ayupi.pse_new.core.model.StorageItem
import dev.ayupi.pse_new.core.model.StorageSummary
import dev.ayupi.pse_new.core.model.StorageUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface StorageRepository {
    val isSyncing: Flow<Boolean>

    fun getInventory(): Flow<List<StorageItem>>

    fun getStorageItemById(id: String): Flow<StorageItem?>

    fun getStorages(): Flow<List<Storage>>

    fun getStorageSummaries(): Flow<List<StorageSummary>>

    fun getItems(): Flow<List<Item>>

    suspend fun updateItemName(id: String, newName: String)

    suspend fun refresh()

    suspend fun saveItem(
        existingInventoryId: String?,
        itemId: String?,
        itemName: String,
        storageId: String,
        quantity: Long,
        itemSize: Int,
        unit: StorageUnit,
        expiration: LocalDate?
    )

    suspend fun deleteItem(id: String)
    fun getItemsInStorage(storageId: String): Flow<List<StorageItem>>
    fun getStorageById(storageId: String): Flow<Storage?>

    suspend fun deleteMasterItem(id: String)
}