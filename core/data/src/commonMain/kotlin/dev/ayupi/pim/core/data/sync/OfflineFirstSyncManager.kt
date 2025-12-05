package dev.ayupi.pim.core.data.sync

import dev.ayupi.pim.core.data.mappers.toDto
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.database.dao.InventoryDao
import dev.ayupi.pim.core.database.dao.ItemDao
import dev.ayupi.pim.core.database.dao.StorageDao
import dev.ayupi.pim.core.database.entity.ItemEntity
import dev.ayupi.pim.core.database.entity.StorageEntity
import dev.ayupi.pim.core.database.entity.StorageItemEntity
import dev.ayupi.pim.core.network.StorageApi
import dev.ayupi.pim.core.network.model.ApiResponse
import dev.ayupi.pim.core.network.model.ItemDto
import dev.ayupi.pim.core.network.model.StorageDto
import dev.ayupi.pim.core.network.model.StorageItemDto
import dev.ayupi.pim.core.network.model.SyncInventoryRequest
import dev.ayupi.pim.core.network.model.SyncItemsRequest
import dev.ayupi.pim.core.network.model.SyncStoragesRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OfflineFirstSyncManager(
    private val storageDao: StorageDao,
    private val inventoryDao: InventoryDao,
    private val itemDao: ItemDao,
    private val api: StorageApi,
    private val userDataRepository: UserDataRepository
) : SyncManager {
    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val mutex = Mutex()

    override suspend fun triggerSync() {
        if (mutex.isLocked) return

        _isSyncing.value = true

        try {
            mutex.withLock {
                try {
                    // 1. PUSH (Hochladen)
                    // Reihenfolge wichtig: Eltern vor Kindern!
                    pushLocalChanges()

                    // 2. PULL (Herunterladen)
                    // Auch hier: Erst Stammdaten holen, damit Inventory verknüpft werden kann.
                    pullRemoteChanges()

                    // 3. Timestamp setzen
                    userDataRepository.onLastSyncUpdate(Clock.System.now())

                } catch (e: Exception) {
                    println("Sync failed (Offline?): ${e.message}")
                    e.printStackTrace()
                }
            }
        } finally {
            _isSyncing.value = false
        }
    }

    // ========================================================================
    // 1. ORCHESTRATOR: PUSH
    // ========================================================================

    private suspend fun pushLocalChanges() {
        // A. Storages hochladen
        pushDirtyStorages()

        // B. Items hochladen
        pushDirtyItems()

        // C. Inventory hochladen (IDs sind jetzt sicher bekannt)
        pushDirtyInventory()
    }

    private suspend fun pushDirtyStorages() {
        val dirtyList = storageDao.getDirty()
        if (dirtyList.isEmpty()) return

        val dtos = dirtyList.map { it.toDto() }
        val response = api.syncStoragesBatch(SyncStoragesRequest(dtos))

        // Cleanup & Update
        storageDao.clearDirty(dirtyList.map { it.id })
        response.data.forEach { processServerStorage(it) }
    }

    private suspend fun pushDirtyItems() {
        val dirtyList = itemDao.getDirty()
        if (dirtyList.isEmpty()) return

        val dtos = dirtyList.map { it.toDto() }
        val response = api.syncItemsBatch(SyncItemsRequest(dtos))

        itemDao.clearDirty(dirtyList.map { it.id })
        response.data.forEach { processServerItem(it) }
    }

    private suspend fun pushDirtyInventory() {
        val dirtyList = inventoryDao.getDirty()
        if (dirtyList.isEmpty()) return

        // Wir müssen Details (Namen) laden für das Nested DTO
        val dtos = dirtyList.mapNotNull { entity ->
            val details = inventoryDao.getByIdWithDetails(entity.id).firstOrNull()
            details?.toDto()
        }

        if (dtos.isNotEmpty()) {
            val response = api.syncInventoryBatch(SyncInventoryRequest(dtos))

            inventoryDao.clearDirty(dirtyList.map { it.id })
            response.data.forEach { processServerInventory(it) }
        }
    }

    // ========================================================================
    // 2. ORCHESTRATOR: PULL (Mit Generic Helper)
    // ========================================================================

    private suspend fun pullRemoteChanges() {
        val lastSync = userDataRepository.data.first().lastSyncTimestamp
        // A. Storages
        pullDataGeneric(
            lastSync = lastSync,
            fetchApiData = { since, cursor -> api.getStorages(since, cursor) },
            processItem = { dto -> processServerStorage(dto) }
        )
        // B. Items
        pullDataGeneric(
            lastSync = lastSync,
            fetchApiData = { since, cursor -> api.getItems(since, cursor) },
            processItem = { dto -> processServerItem(dto) }
        )
        // C. Inventory
        pullDataGeneric(
            lastSync = lastSync,
            fetchApiData = { since, cursor -> api.getInventory(since, cursor) },
            processItem = { dto -> processServerInventory(dto) }
        )
    }

    /**
     * Generischer Loop für Pagination & Delta Sync.
     * Spart uns das dreifache Schreiben der while-Schleife.
     */
    private suspend fun <T> pullDataGeneric(
        lastSync: Instant,
        fetchApiData: suspend (String?, String?) -> ApiResponse<List<T>>,
        processItem: suspend (T) -> Unit
    ) {
        val sinceParam = if (lastSync == Instant.DISTANT_PAST) null else lastSync.toString()
        var currentCursor: String? = null
        var hasMore = true

        while (hasMore) {
            val response = fetchApiData(sinceParam, currentCursor)

            response.data.forEach { dto ->
                processItem(dto)
            }

            currentCursor = response.meta?.nextCursor
            if (currentCursor == null) hasMore = false
        }
    }

    // ========================================================================
    // 3. LOGIK: Server Response Verarbeiten
    // ========================================================================

    private suspend fun processServerStorage(dto: StorageDto) {
        val id = Uuid.parse(dto.id)
        if (dto.deletedAt != null) {
            storageDao.hardDelete(id)
        } else {
            storageDao.upsert(StorageEntity(
                id = id,
                name = dto.name,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                deletedAt = null,
                isDirty = false,
                isDeleted = false
            ))
        }
    }

    private suspend fun processServerItem(dto: ItemDto) {
        val id = Uuid.parse(dto.id)
        if (dto.deletedAt != null) {
            itemDao.hardDelete(id)
        } else {
            itemDao.upsert(ItemEntity(
                id = id,
                name = dto.name,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                deletedAt = null,
                isDirty = false,
                isDeleted = false
            ))
        }
    }

    private suspend fun processServerInventory(dto: StorageItemDto) {
        val id = Uuid.parse(dto.id)

        // 1. Check Soft Delete
        if (dto.deletedAt != null) {
            inventoryDao.hardDelete(id)
            return
        }

        val itemEntity = ItemEntity(
            id = Uuid.parse(dto.item.id),
            name = dto.item.name,
            updatedAt = dto.updatedAt,
            createdAt = dto.updatedAt,
            deletedAt = null,
            isDirty = false
        )
        itemDao.upsert(itemEntity)

        val storageEntity = StorageEntity(
            id = Uuid.parse(dto.storage.id),
            name = dto.storage.name,
            updatedAt = dto.updatedAt,
            createdAt = dto.updatedAt,
            deletedAt = null,
            isDirty = false
        )
        storageDao.upsert(storageEntity)

        // 3. Inventory Upsert
        val entity = StorageItemEntity(
            id = id,
            storageId = Uuid.parse(dto.storage.id),
            itemId = Uuid.parse(dto.item.id),

            quantity = dto.quantityInfo.count,
            itemSize = dto.quantityInfo.sizePerUnit,
            unit = dto.quantityInfo.unit,
            expirationDate = dto.expirationDate,

            // Server Authority: Wir übernehmen Zeitstempel
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,

            isDirty = false,
            isDeleted = false,
            deletedAt = null
        )
        inventoryDao.upsert(entity)
    }
}