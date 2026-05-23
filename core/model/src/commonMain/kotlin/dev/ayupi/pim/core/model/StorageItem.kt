package dev.ayupi.pim.core.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class StorageItem(
    val id: String,
    val item: Item,
    val storage: Storage,
    val quantity: Long,
    val expirationDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val itemSize: Int get() = item.itemSize
    val unit: StorageUnit get() = item.unit
}
