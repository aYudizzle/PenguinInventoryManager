package dev.ayupi.pim.core.model

import kotlin.time.Instant

data class StorageSummary(
    val id: String,
    val name: String,
    val productCount: Int,
    val updatedAt: Instant
)
