package dev.ayupi.pim.di

import dev.ayupi.pim.core.data.di.dataModule
import dev.ayupi.pim.feature.inventory.di.inventoryModule
import dev.ayupi.pim.feature.itementry.di.itemEntryModule
import dev.ayupi.pim.feature.itemmaster.di.itemMasterModule
import dev.ayupi.pim.feature.settings.di.settingsModule
import dev.ayupi.pim.feature.storagedetails.di.storageDetailsModule
import dev.ayupi.pim.feature.storageoverview.di.storageOverviewModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            storageDetailsModule,
            storageOverviewModule,
            itemEntryModule,
            settingsModule,
            dataModule,
            itemMasterModule,
            inventoryModule,
        )
    }
}