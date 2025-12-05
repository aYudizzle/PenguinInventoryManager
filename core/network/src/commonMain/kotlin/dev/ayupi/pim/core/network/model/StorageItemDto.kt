package dev.ayupi.pim.core.network.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class StorageItemDto(
    val id: String,
    val item: NestedItem,
    val storage: NestedStorage,
    @SerialName("quantity_info")
    val quantityInfo: NestedQuantity,

    // Timestamps
    @SerialName("expiration_date")
    val expirationDate: LocalDate?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null
)
