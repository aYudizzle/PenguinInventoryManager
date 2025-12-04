package dev.ayupi.pse_new.core.data.repository

import dev.ayupi.pse_new.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface UserDataRepository {
    val data: Flow<UserData>
    suspend fun isDarkModeEnabled(isDarkMode: Boolean)
    suspend fun onLastSyncUpdate(lastSync: Instant)

    suspend fun setExpirationWarningDays(days: Int)
}