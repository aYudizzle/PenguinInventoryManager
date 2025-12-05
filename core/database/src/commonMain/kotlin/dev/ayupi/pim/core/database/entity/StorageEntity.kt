@file:OptIn(ExperimentalUuidApi::class)

package dev.ayupi.pim.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "storages")
data class StorageEntity(
    @PrimaryKey(autoGenerate = false) val id: Uuid = Uuid.random(),
    val name: String,

    // # Sync Fields
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val isDirty: Boolean = false,
    val isDeleted: Boolean = false,
)