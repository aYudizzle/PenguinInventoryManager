package dev.ayupi.pim.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StorageItemWithDetails(
    @Embedded
    val entry: StorageItemEntity,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item: ItemEntity,
    @Relation(
        parentColumn = "storageId",
        entityColumn = "id"
    )
    val storage: StorageEntity
)