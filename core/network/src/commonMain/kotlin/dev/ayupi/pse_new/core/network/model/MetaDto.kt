package dev.ayupi.pse_new.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaDto(
    @SerialName("path")
    val path: String,

    @SerialName("next_cursor")
    val nextCursor: String? = null,

    @SerialName("prev_cursor")
    val prevCursor: String? = null,

    @SerialName("per_page")
    val perPage: Int? = null
)
