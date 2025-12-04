package dev.ayupi.pse_new.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncItemsRequest(
    val items: List<ItemDto>
)