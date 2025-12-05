package dev.ayupi.pim.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T,
    val meta: MetaDto? = null
)
