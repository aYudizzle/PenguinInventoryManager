package dev.ayupi.pim.core.ui.mapper

import dev.ayupi.pim.core.ui.model.DisplayableDate
import dev.ayupi.pim.core.ui.model.ExpirationStatus
import dev.ayupi.pim.core.ui.util.formatDateMedium
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

fun LocalDate.toDisplayableDate() = DisplayableDate(this, this.formatDateMedium())

fun LocalDate?.toExpirationStatus(daysUntilExpirationForWarning: Int = 10): ExpirationStatus {
    if(this == null) return ExpirationStatus.NOT_SET
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntil = today.daysUntil(this)
    return when {
        daysUntil < 0 -> ExpirationStatus.EXPIRED
        daysUntil <= daysUntilExpirationForWarning -> ExpirationStatus.WARNING
        else -> ExpirationStatus.VALID
    }
}