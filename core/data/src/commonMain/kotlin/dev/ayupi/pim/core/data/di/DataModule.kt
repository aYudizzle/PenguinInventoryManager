package dev.ayupi.pim.core.data.di

import dev.ayupi.pim.core.data.repository.OfflineFirstStorageRepository
import dev.ayupi.pim.core.data.repository.OfflineFirstUserDataRepository
import dev.ayupi.pim.core.data.repository.StorageRepository
import dev.ayupi.pim.core.data.repository.UserDataRepository
import dev.ayupi.pim.core.data.sync.OfflineFirstSyncManager
import dev.ayupi.pim.core.data.sync.SyncManager
import dev.ayupi.pim.core.data.util.ConnectivityNetworkMonitor
import dev.ayupi.pim.core.data.util.NetworkMonitor
import dev.ayupi.pim.core.database.di.databaseModule
import dev.ayupi.pim.core.datastore.di.dataStoreModule
import dev.ayupi.pim.core.network.di.networkModule
import org.koin.dsl.module

val dataModule = module {
    includes(databaseModule, networkModule, dataStoreModule)

    single<NetworkMonitor> { ConnectivityNetworkMonitor() }
    single<UserDataRepository> { OfflineFirstUserDataRepository(get()) }
    single<SyncManager> { OfflineFirstSyncManager(get(), get(), get(), get(), get()) }
    single<StorageRepository> { OfflineFirstStorageRepository(get(), get(), get(), get()) }
}
