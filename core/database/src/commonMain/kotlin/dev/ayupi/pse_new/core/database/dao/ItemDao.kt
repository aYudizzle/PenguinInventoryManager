package dev.ayupi.pse_new.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.ayupi.pse_new.core.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Dao
interface ItemDao : BaseSyncDao<ItemEntity> {
    // --- SYNC PUSH ---
    @Query("SELECT * FROM items WHERE isDirty = 1")
    suspend fun getDirty(): List<ItemEntity>

    // --- SYNC CLEANUP ---
    @Query("UPDATE items SET isDirty = 0 WHERE id IN (:ids)")
    suspend fun clearDirty(ids: List<Uuid>)

    // --- HARD DELETE (Server bestätigt) ---
    @Query("DELETE FROM items WHERE id = :id")
    suspend fun hardDelete(id: Uuid)

    // --- UI / LOGIC ---
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getById(id: Uuid): ItemEntity?

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("""
        UPDATE items 
        SET isDeleted = 1, 
            isDirty = 1, 
            deletedAt = :now, 
            updatedAt = :now 
        WHERE id = :id
    """)
    suspend fun softDelete(id: Uuid, now: Instant)
}