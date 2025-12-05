package dev.ayupi.pim.core.ui.model

import androidx.compose.ui.graphics.Color

enum class ExpirationStatus {
    EXPIRED,
    WARNING,
    VALID,
    NOT_SET,
}

fun ExpirationStatus.getColor(): Color {
    return when(this) {
        ExpirationStatus.EXPIRED -> Color(0xFFB00020)
        ExpirationStatus.WARNING -> Color(0xFFF57C00)
        ExpirationStatus.VALID -> Color(0xFF388E3C)
        ExpirationStatus.NOT_SET -> Color.Gray
    }
}