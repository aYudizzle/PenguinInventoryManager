package dev.ayupi.pim.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class StorageDto(
    val id: String,
    val name: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("deleted_at") val deletedAt: Instant? = null
)
