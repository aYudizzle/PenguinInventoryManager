package dev.ayupi.pim.core.data.repository

import dev.ayupi.pim.core.data.mappers.toDomain
import dev.ayupi.pim.core.datastore.SettingsDataSource
import dev.ayupi.pim.core.model.UserData
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