package dev.ayupi.pim.feature.inventory.di

import dev.ayupi.pim.feature.inventory.InventoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inventoryModule = module {
    viewModelOf(::InventoryViewModel)
}