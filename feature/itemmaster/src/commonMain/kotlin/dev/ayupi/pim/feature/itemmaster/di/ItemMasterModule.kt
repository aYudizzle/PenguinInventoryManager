package dev.ayupi.pim.feature.itemmaster.di

import dev.ayupi.pim.feature.itemmaster.ItemMasterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val itemMasterModule = module {
    viewModelOf(::ItemMasterViewModel)
}