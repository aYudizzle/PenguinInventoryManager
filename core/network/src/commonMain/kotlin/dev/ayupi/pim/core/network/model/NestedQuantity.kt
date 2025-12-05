package dev.ayupi.pim.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NestedQuantity(
    val count: Long,
    val unit: String,
    @SerialName("size_per_unit")
    val sizePerUnit: Int,
)
