package dev.ayupi.pse_new.core.data.sync

import kotlinx.coroutines.flow.StateFlow

interface SyncManager {
    val isSyncing: StateFlow<Boolean>
    suspend fun triggerSync()
}