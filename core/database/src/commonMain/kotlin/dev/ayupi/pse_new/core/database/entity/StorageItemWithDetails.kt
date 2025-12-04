package dev.ayupi.pse_new.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StorageItemWithDetails(
    @Embedded
    val entry: StorageItemEntity,
    @Relation(
        parentColumn = "itemId", // Feld in StorageItemEntity
        entityColumn = "id"      // Feld in ItemEntity
    )
    val item: ItemEntity,
    @Relation(
        parentColumn = "storageId",
        entityColumn = "id"
    )
    val storage: StorageEntity
)