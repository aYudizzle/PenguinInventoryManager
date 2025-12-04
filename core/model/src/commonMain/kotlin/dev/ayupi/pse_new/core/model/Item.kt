package dev.ayupi.pse_new.core.model

import kotlin.time.Instant

data class Item(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
