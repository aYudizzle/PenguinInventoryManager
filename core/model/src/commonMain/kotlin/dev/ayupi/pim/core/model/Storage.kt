package dev.ayupi.pim.core.model

import kotlin.time.Instant

data class Storage(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
