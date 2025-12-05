package dev.ayupi.pim.core.datastore

import androidx.datastore.core.DataStore
import dev.ayupi.pim.core.datastore.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

class SettingsDataSource(
    private val dataStore: DataStore<UserData>
) {
    val userData: Flow<UserData> = dataStore.data

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.updateData { currentSettings ->
            currentSettings.copy(isDarkMode = isDarkMode)
        }
    }

    suspend fun updateLastSyncTimeStamp(timestamp: Instant) {
        dataStore.updateData { currentSettings ->
            currentSettings.copy(lastSyncTimestamp = timestamp)

        }
    }

    suspend fun setExpirationWarningDays(days: Int) {
        dataStore.updateData { currentSettings ->
            currentSettings.copy(expirationWarningDays = days)
        }
    }
}