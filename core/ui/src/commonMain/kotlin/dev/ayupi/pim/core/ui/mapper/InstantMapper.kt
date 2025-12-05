package dev.ayupi.pim.core.ui.mapper

import dev.ayupi.pim.core.ui.model.DisplayableInstant
import dev.ayupi.pim.core.ui.util.formatDateTimeShort
import kotlin.time.Instant

fun Instant.toDisplayableInstant() = DisplayableInstant(this, formatDateTimeShort(), "Datum")