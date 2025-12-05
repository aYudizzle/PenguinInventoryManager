package dev.ayupi.pim.core.datastore.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class UserData(
    val lastSyncTimestamp: Instant = Instant.DISTANT_PAST,
    val isDarkMode: Boolean = false,
    val expirationWarningDays: Int = 10
)
