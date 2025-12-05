package dev.ayupi.pim.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncInventoryRequest(
    val items: List<StorageItemDto>
)
