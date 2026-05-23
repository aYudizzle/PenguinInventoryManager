package dev.ayupi.pim.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NestedItem(
    val id: String,
    val name: String,
    val barcode: String? = null,
    @SerialName("item_size") val itemSize: Int = 1,
    val unit: String = "g",
)
