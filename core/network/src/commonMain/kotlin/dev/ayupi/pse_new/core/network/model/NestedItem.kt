package dev.ayupi.pse_new.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NestedItem(
    val id: String,
    val name: String,
)
