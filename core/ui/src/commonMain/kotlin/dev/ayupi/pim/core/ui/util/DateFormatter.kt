package dev.ayupi.pim.core.ui.util

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

expect fun Instant.formatDateTimeShort(): String
expect fun LocalDate.formatDateMedium(): String