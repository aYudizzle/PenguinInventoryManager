package dev.ayupi.pse_new.core.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant

actual fun Instant.formatDateTimeShort(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault()) // Sprache des Nutzers (Deutsch/Englisch)
        .withZone(ZoneId.systemDefault()) // Zeitzone des Handys

    // Wichtig: toJavaInstant() konvertiert KMP-Instant zu Java-Instant
    return formatter.format(this.toJavaInstant())
}

// LocalDate (Tag) formatieren
actual fun LocalDate.formatDateMedium(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())

    return formatter.format(this.toJavaLocalDate())
}