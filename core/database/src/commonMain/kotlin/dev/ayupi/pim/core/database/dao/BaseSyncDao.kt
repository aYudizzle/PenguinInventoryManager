package dev.ayupi.pim.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Upsert

interface BaseSyncDao<T> {
    @Upsert
    suspend fun upsert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>)


    @Delete
    suspend fun delete(entity: T)

    // Helper Transaction
    @Transaction
    suspend fun <R> transaction(block: suspend () -> R): R {
        return block()
    }
}