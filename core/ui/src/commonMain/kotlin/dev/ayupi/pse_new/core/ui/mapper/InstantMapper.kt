package dev.ayupi.pse_new.core.ui.mapper

import dev.ayupi.pse_new.core.ui.model.DisplayableInstant
import dev.ayupi.pse_new.core.ui.util.formatDateTimeShort
import kotlin.time.Instant

fun Instant.toDisplayableInstant() = DisplayableInstant(this, formatDateTimeShort(), "Datum")