package dev.ayupi.pse_new.core.ui.util

import kotlin.time.Clock
import kotlin.time.Instant

fun Instant.toRelativeTime(now: Instant = Clock.System.now()): String {
    val diff = now - this
    return when {
        diff.inWholeSeconds < 60 -> "Gerade eben"
        diff.inWholeMinutes < 60 -> "Vor ${diff.inWholeMinutes} Min."
        diff.inWholeHours < 24 -> "Vor ${diff.inWholeHours} Std."
        diff.inWholeDays < 7 -> "Vor ${diff.inWholeDays} Tagen"
        else -> this.formatDateTimeShort() // Fallback auf Datum (deine existierende Funktion)
    }
}