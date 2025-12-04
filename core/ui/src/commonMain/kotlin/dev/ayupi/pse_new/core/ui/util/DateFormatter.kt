package dev.ayupi.pse_new.core.ui.util

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

expect fun Instant.formatDateTimeShort(): String // z.B. "25.11.24 14:00"
expect fun LocalDate.formatDateMedium(): String  // z.B. "25. Nov 2024"