package dev.ayupi.pse_new.di

import dev.ayupi.pse_new.core.data.di.dataModule
import dev.ayupi.pse_new.feature.inventory.di.inventoryModule
import dev.ayupi.pse_new.feature.itementry.di.itemEntryModule
import dev.ayupi.pse_new.feature.itemmaster.di.itemMasterModule
import dev.ayupi.pse_new.feature.settings.di.settingsModule
import dev.ayupi.pse_new.feature.storagedetails.di.storageDetailsModule
import dev.ayupi.pse_new.feature.storageoverview.di.storageOverviewModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

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