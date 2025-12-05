package dev.ayupi.pim.core.data.mappers

import dev.ayupi.pim.core.datastore.model.UserData
import dev.ayupi.pim.core.model.UserData as DomainUserData

fun UserData.toDomain() = DomainUserData(
    lastSyncTimestamp = lastSyncTimestamp,
    isDarkMode = isDarkMode,
    expirationWarningDays = expirationWarningDays
)