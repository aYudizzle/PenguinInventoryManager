package dev.ayupi.pse_new.core.data.di

import dev.ayupi.pse_new.core.data.repository.OfflineFirstStorageRepository
import dev.ayupi.pse_new.core.data.repository.OfflineFirstUserDataRepository
import dev.ayupi.pse_new.core.data.repository.StorageRepository
import dev.ayupi.pse_new.core.data.repository.UserDataRepository
import dev.ayupi.pse_new.core.data.sync.OfflineFirstSyncManager
import dev.ayupi.pse_new.core.data.sync.SyncManager
import dev.ayupi.pse_new.core.data.util.ConnectivityNetworkMonitor
import dev.ayupi.pse_new.core.data.util.NetworkMonitor
import dev.ayupi.pse_new.core.database.di.databaseModule
import dev.ayupi.pse_new.core.datastore.di.dataStoreModule
import dev.ayupi.pse_new.core.network.di.networkModule
import org.koin.dsl.module

val dataModule = module {
    includes(databaseModule, networkModule, dataStoreModule)

    single<NetworkMonitor> { ConnectivityNetworkMonitor() }
    single<UserDataRepository> { OfflineFirstUserDataRepository(get()) }
    single<SyncManager> { OfflineFirstSyncManager(get(), get(), get(), get(), get()) }
    single<StorageRepository> { OfflineFirstStorageRepository(get(), get(), get(), get()) }
}
