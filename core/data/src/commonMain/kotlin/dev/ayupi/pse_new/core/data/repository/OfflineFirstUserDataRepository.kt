package dev.ayupi.pse_new.core.data.repository

import dev.ayupi.pse_new.core.data.mappers.toDomain
import dev.ayupi.pse_new.core.datastore.SettingsDataSource
import dev.ayupi.pse_new.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class OfflineFirstUserDataRepository(
    private val settingsDataSource: SettingsDataSource
): UserDataRepository {
    override val data: Flow<UserData> = settingsDataSource.userData.map { it.toDomain() }

    override suspend fun onLastSyncUpdate(lastSync: Instant) {
        settingsDataSource.updateLastSyncTimeStamp(lastSync)
    }

    override suspend fun isDarkModeEnabled(isDarkMode: Boolean) {
        settingsDataSource.setDarkMode(isDarkMode)
    }

    override suspend fun setExpirationWarningDays(days: Int) {
        settingsDataSource.setExpirationWarningDays(days)
    }
}