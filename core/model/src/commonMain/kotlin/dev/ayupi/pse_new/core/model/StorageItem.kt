package dev.ayupi.pse_new.core.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class StorageItem(
    val id: String, // usually UUID
    val item: Item,
    val storage: Storage,
    val itemSize: Int,
    val quantity: Long,
    val unit: StorageUnit,
    val expirationDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
