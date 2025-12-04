package dev.ayupi.pse_new.core.data.repository

import dev.ayupi.pse_new.core.data.mappers.toDomain
import dev.ayupi.pse_new.core.data.mappers.toDomainList
import dev.ayupi.pse_new.core.data.sync.SyncManager
import dev.ayupi.pse_new.core.database.dao.InventoryDao
import dev.ayupi.pse_new.core.database.dao.ItemDao
import dev.ayupi.pse_new.core.database.dao.StorageDao
import dev.ayupi.pse_new.core.database.entity.ItemEntity
import dev.ayupi.pse_new.core.database.entity.StorageItemEntity
import dev.ayupi.pse_new.core.model.Item
import dev.ayupi.pse_new.core.model.Storage
import dev.ayupi.pse_new.core.model.StorageItem
import dev.ayupi.pse_new.core.model.StorageSummary
import dev.ayupi.pse_new.core.model.StorageUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.Uuid

class OfflineFirstStorageRepository(
    private val inventoryDao: InventoryDao, // Für Bestände & Transaktionen
    private val itemDao: ItemDao,           // Für Master-Items ("Milch")
    private val storageDao: StorageDao,     // Für Lagerorte ("Kühlschrank") - falls nötig
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
        syncManager.triggerSync()
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
            // Wir nutzen die Transaction-Methode von irgendeinem DAO (sie gehören zur selben DB)
            inventoryDao.transaction {
                // A. Master Data Handling (Item)
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
                    // ✅ Hier rufen wir jetzt das spezialisierte ItemDao auf
                    itemDao.upsert(newItemEntity)
                    newUuid
                } else {
                    Uuid.parse(itemId)
                }

                println("finalItemId: $finalItemId added")

                // B. Inventory Handling
                val inventoryId = existingInventoryId?.let { Uuid.parse(it) } ?: Uuid.random()

                // ✅ Zugriff auf InventoryDao
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
                println("InventoryEntity: $inventoryEntity")
                inventoryDao.upsert(inventoryEntity)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
        }

        // C. Sync
        try {
            syncManager.triggerSync()
        } catch (e: Exception) {
            // Ignore offline errors
        }
    }

    override suspend fun deleteItem(id: String) {
        val now = Clock.System.now()

        inventoryDao.softDelete(
            id = Uuid.parse(id),
            now = now
        )

        try {
            syncManager.triggerSync()
        } catch (e: Exception) {
        }
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

        try {
            syncManager.triggerSync()
        } catch (e: Exception) {}
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

        itemDao.softDelete(uuid, now)

        try { syncManager.triggerSync() } catch (e: Exception) {}
    }
}