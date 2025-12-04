package dev.ayupi.pse_new.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncStoragesRequest(
    val storages: List<StorageDto>
)