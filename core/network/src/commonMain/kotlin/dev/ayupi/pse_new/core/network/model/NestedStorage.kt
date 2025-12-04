package dev.ayupi.pse_new.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NestedStorage(
    val id: String,
    val name: String,
)
