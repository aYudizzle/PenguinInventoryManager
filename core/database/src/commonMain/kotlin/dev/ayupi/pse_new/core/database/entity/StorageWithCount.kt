package dev.ayupi.pse_new.core.database.entity

import androidx.room.Embedded

data class StorageWithCount(
    @Embedded
    val storage: StorageEntity,

    // Das zusätzliche berechnete Feld
    val itemCount: Int
)