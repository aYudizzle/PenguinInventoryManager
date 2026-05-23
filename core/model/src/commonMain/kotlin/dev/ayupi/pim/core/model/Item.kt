package dev.ayupi.pim.core.model

import kotlin.time.Instant

data class Item(
    val id: String,
    val name: String,
    val barcode: String?,
    val itemSize: Int = 1,
    val unit: StorageUnit = StorageUnit.GRAM,
    val createdAt: Instant,
    val updatedAt: Instant,
)
