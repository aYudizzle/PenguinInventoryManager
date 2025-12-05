package dev.ayupi.pim.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ayupi.pim.core.database.entity.StorageItemEntity
import dev.ayupi.pim.core.database.entity.StorageItemWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Dao
interface InventoryDao : BaseSyncDao<StorageItemEntity> {

    // ========================================================================
    // UI QUERIES (Relational)
    // ========================================================================

    @Transaction
    @Query("""
        SELECT * FROM storage_items 
        WHERE isDeleted = 0 
        ORDER BY updatedAt DESC
    """)
    fun getAllItemsWithDetailsFlow(): Flow<List<StorageItemWithDetails>>

    @Transaction
    @Query("SELECT * FROM storage_items WHERE id = :id")
    fun getByIdWithDetails(id: Uuid): Flow<StorageItemWithDetails?>

    @Query("SELECT * FROM storage_items WHERE id = :id")
    suspend fun getEntryById(id: Uuid): StorageItemEntity?

    // ========================================================================
    // SYNC SPECIFIC (Push/Clean/Delete)
    // ========================================================================

    @Query("SELECT * FROM storage_items WHERE isDirty = 1")
    suspend fun getDirty(): List<StorageItemEntity>

    @Query("UPDATE storage_items SET isDirty = 0 WHERE id IN (:ids)")
    suspend fun clearDirty(ids: List<Uuid>)

    @Query("UPDATE storage_items SET isDirty = 0 WHERE id = :id")
    suspend fun clearDirty(id: Uuid) // Single Version

    @Query("DELETE FROM storage_items WHERE id = :id")
    suspend fun hardDelete(id: Uuid)

    // ========================================================================
    // LOKALE LOGIK (Soft Delete)
    // ========================================================================

    @Query("""
        UPDATE storage_items 
        SET isDeleted = 1, 
            isDirty = 1, 
            deletedAt = :now, 
            updatedAt = :now 
        WHERE id = :id
    """)
    suspend fun softDelete(id: Uuid, now: Instant)

    @Query("""
        UPDATE storage_items 
        SET isDeleted = 1, 
            isDirty = 1, 
            deletedAt = :now, 
            updatedAt = :now 
        WHERE storageId = :storageId AND isDeleted = 0
    """)
    suspend fun softDeleteByStorageId(storageId: Uuid, now: Instant)

    @Query("""
        UPDATE storage_items 
        SET isDeleted = 1, 
            isDirty = 1, 
            deletedAt = :now, 
            updatedAt = :now 
        WHERE itemId = :itemId AND isDeleted = 0
    """)
    suspend fun softDeleteByItemId(itemId: Uuid, now: Instant)


    // ==================================
    // UI LOGIK
    // ==================================
    @Transaction
    @Query("""
        SELECT * FROM storage_items 
        WHERE storageId = :storageId 
          AND isDeleted = 0 
        ORDER BY updatedAt DESC
    """)
    fun getItemsByStorageId(storageId: Uuid): Flow<List<StorageItemWithDetails>>
}