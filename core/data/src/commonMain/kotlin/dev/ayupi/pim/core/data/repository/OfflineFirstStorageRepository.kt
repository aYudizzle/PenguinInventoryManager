package dev.ayupi.pim.core.data.repository

import dev.ayupi.pim.core.data.mappers.toDomain
import dev.ayupi.pim.core.data.mappers.toDomainList
import dev.ayupi.pim.core.data.sync.SyncManager
import dev.ayupi.pim.core.database.dao.InventoryDao
import dev.ayupi.pim.core.database.dao.ItemDao
import dev.ayupi.pim.core.database.dao.StorageDao
import dev.ayupi.pim.core.database.entity.ItemEntity
import dev.ayupi.pim.core.database.entity.StorageEntity
import dev.ayupi.pim.core.database.entity.StorageItemEntity
import dev.ayupi.pim.core.model.Item
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.model.StorageItem
import dev.ayupi.pim.core.model.StorageSummary
import dev.ayupi.pim.core.model.StorageUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.Uuid

class OfflineFirstStorageRepository(
    private val inventoryDao: InventoryDao,
    private val itemDao: ItemDao,
    private val storageDao: StorageDao,
    private val syncManager: SyncManager
) : StorageRepository {

    override val isSyncing: Flow<Boolean> = syncManager.isSyncing

    override fun getInventory(): Flow<List<StorageItem>> {
        return inventoryDao.getAllItemsWithDetailsFlow()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getStorages(): Flow<List<Storage>> {
        return storageDao.getAll().map { it.toDomain() }
    }

    override fun getStorageSummaries(): Flow<List<StorageSummary>> {
        return storageDao.getStoragesWithCountFlow().map { it.toDomainList() }
    }

    override fun getItems(): Flow<List<Item>> =
        itemDao.getItems().map { it.toDomain() }

    override suspend fun refresh() {
        trySync()
    }

    override suspend fun saveItem(
        existingInventoryId: String?,
        itemId: String?,
        itemName: String,
        storageId: String,
        quantity: Long,
        itemSize: Int,
        unit: StorageUnit,
        expiration: LocalDate?
    ) {
        val now = Clock.System.now()
        try {
            inventoryDao.transaction {
                val finalItemId = if (itemId == null) {
                    val newUuid = Uuid.random()
                    val newItemEntity = ItemEntity(
                        id = newUuid,
                        name = itemName,
                        createdAt = now,
                        updatedAt = now,
                        isDirty = true,
                        isDeleted = false,
                    )
                    itemDao.upsert(newItemEntity)
                    newUuid
                } else {
                    Uuid.parse(itemId)
                }

                val inventoryId = existingInventoryId?.let { Uuid.parse(it) } ?: Uuid.random()

                val createdAt = if (existingInventoryId != null) {
                    inventoryDao.getEntryById(inventoryId)?.createdAt ?: now
                } else {
                    now
                }

                val inventoryEntity = StorageItemEntity(
                    id = inventoryId,
                    itemId = finalItemId,
                    storageId = Uuid.parse(storageId),
                    quantity = quantity,
                    unit = unit.abbreviation,
                    itemSize = itemSize,
                    expirationDate = expiration,

                    createdAt = createdAt,
                    updatedAt = now,
                    isDirty = true,
                    isDeleted = false,
                    deletedAt = null
                )
                inventoryDao.upsert(inventoryEntity)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
        }

        trySync()
    }

    override suspend fun deleteStorageItem(id: String) {
        val now = Clock.System.now()

        inventoryDao.softDelete(
            id = Uuid.parse(id),
            now = now
        )

        trySync()
    }

    override suspend fun updateItemName(id: String, newName: String) {
        val now = Clock.System.now()
        val uuid = Uuid.parse(id)

        val existing = itemDao.getById(uuid) ?: return

        val updatedItem = existing.copy(
            name = newName,
            updatedAt = now,
            isDirty = true
        )

        itemDao.upsert(updatedItem)

        trySync()
    }


    override fun getStorageItemById(id: String): Flow<StorageItem?> = try {
        val uuid = Uuid.parse(id)
        inventoryDao.getByIdWithDetails(uuid).map { it?.toDomain() }
    } catch (e: Exception) {
        flowOf(null)
    }

    override fun getItemsInStorage(storageId: String): Flow<List<StorageItem>> = try {
        val uuid = Uuid.parse(storageId)
        inventoryDao.getItemsByStorageId(uuid).map { list ->
            list.map {
                it.toDomain()
            }
        }
    } catch (e: Exception) {
        flowOf(emptyList())
    }

    override fun getStorageById(storageId: String): Flow<Storage?> = try {
        val uuid = Uuid.parse(storageId)
        storageDao.getById(uuid).map { it?.toDomain() }
    } catch (e: Exception) {
        flowOf(null)
    }

    override suspend fun deleteMasterItem(id: String) {
        val now = Clock.System.now()
        val uuid = Uuid.parse(id)

        itemDao.transaction {
            itemDao.softDelete(uuid, now)
            inventoryDao.softDeleteByItemId(uuid, now)
        }

        trySync()
    }

    override suspend fun addStorage(name: String) {
        val now = Clock.System.now()

        // Neue Entity bauen
        val newStorage = StorageEntity(
            id = Uuid.random(),
            name = name,
            createdAt = now,
            updatedAt = now,
            isDirty = true,
            isDeleted = false,

            deletedAt = null
        )

        storageDao.upsert(newStorage)

        trySync()
    }

    override suspend fun updateStorageName(id: String, newName: String) {
        val uuid = Uuid.parse(id)
        val now = Clock.System.now()

        val existing = storageDao.getStorageById(uuid) ?: return

        val updatedStorage = existing.copy(
            name = newName,
            updatedAt = now,
            isDirty = true
        )

        storageDao.upsert(updatedStorage)
        trySync()
    }

    override suspend fun deleteStorage(id: String) {
        val uuid = Uuid.parse(id)
        val now = Clock.System.now()
        storageDao.transaction {
            storageDao.softDelete(uuid, now)
            inventoryDao.softDeleteByStorageId(uuid, now)
        }
        trySync()
    }

    private suspend fun trySync() {
        try {
            syncManager.triggerSync()
        } catch (e: Exception) {
            // Ignorieren (Offline ist okay)
            // Ggf. Logging mit Napier
        }
    }
}