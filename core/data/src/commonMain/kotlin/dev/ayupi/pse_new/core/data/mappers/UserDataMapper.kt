package dev.ayupi.pse_new.core.data.mappers

import dev.ayupi.pse_new.core.datastore.model.UserData
import dev.ayupi.pse_new.core.model.UserData as DomainUserData

fun UserData.toDomain() = DomainUserData(
    lastSyncTimestamp = lastSyncTimestamp,
    isDarkMode = isDarkMode,
    expirationWarningDays = expirationWarningDays
)