package dev.ayupi.pse_new.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.ayupi.pse_new.core.database.entity.StorageEntity
import dev.ayupi.pse_new.core.database.entity.StorageWithCount
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface StorageDao : BaseSyncDao<StorageEntity> {

    // --- SYNC PUSH ---
    @Query("SELECT * FROM storages WHERE isDirty = 1")
    suspend fun getDirty(): List<StorageEntity>

    // --- SYNC CLEANUP ---
    @Query("UPDATE storages SET isDirty = 0 WHERE id IN (:ids)")
    suspend fun clearDirty(ids: List<Uuid>)

    // --- HARD DELETE ---
    @Query("DELETE FROM storages WHERE id = :id")
    suspend fun hardDelete(id: Uuid)

    // --- UI / LOGIC ---
    @Query("SELECT * FROM storages WHERE id = :id")
    fun getById(id: Uuid): Flow<StorageEntity?>

    @Query("SELECT * FROM storages ORDER BY name DESC")
    fun getAll(): Flow<List<StorageEntity>>

    @Query("""
        SELECT 
            s.*, 
            COUNT(si.id) as itemCount 
        FROM storages s
        LEFT JOIN storage_items si ON s.id = si.storageId AND si.isDeleted = 0
        WHERE s.isDeleted = 0
        GROUP BY s.id
        ORDER BY s.name ASC
    """)
    fun getStoragesWithCountFlow(): Flow<List<StorageWithCount>>
}