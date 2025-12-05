package dev.ayupi.pim.core.ui.model

import kotlin.time.Instant

data class DisplayableInstant(
    val raw: Instant,
    val formatted: String,
    val contentDescription: String,
)
