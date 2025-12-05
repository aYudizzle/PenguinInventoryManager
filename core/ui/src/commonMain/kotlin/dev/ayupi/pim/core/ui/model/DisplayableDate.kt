package dev.ayupi.pim.core.ui.model

import kotlinx.datetime.LocalDate

data class DisplayableDate(
    val raw: LocalDate,
    val formatted: String,
)
