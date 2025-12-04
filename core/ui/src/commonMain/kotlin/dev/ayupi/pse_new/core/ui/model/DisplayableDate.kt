package dev.ayupi.pse_new.core.ui.model

import kotlinx.datetime.LocalDate

data class DisplayableDate(
    val raw: LocalDate,
    val formatted: String,
)
