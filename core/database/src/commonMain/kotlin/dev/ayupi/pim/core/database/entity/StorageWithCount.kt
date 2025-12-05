package dev.ayupi.pim.core.database.entity

import androidx.room.Embedded

data class StorageWithCount(
    @Embedded
    val storage: StorageEntity,
    val itemCount: Int
)