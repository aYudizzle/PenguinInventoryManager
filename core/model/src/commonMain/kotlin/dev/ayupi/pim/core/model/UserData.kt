package dev.ayupi.pim.core.model

import kotlin.time.Instant

data class UserData(
    val lastSyncTimestamp: Instant,
    val isDarkMode: Boolean,
    val expirationWarningDays: Int
)
