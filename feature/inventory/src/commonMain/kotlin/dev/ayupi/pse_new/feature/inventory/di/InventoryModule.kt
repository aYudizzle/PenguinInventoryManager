package dev.ayupi.pse_new.feature.inventory.di

import dev.ayupi.pse_new.feature.inventory.InventoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inventoryModule = module {
    viewModelOf(::InventoryViewModel)
}