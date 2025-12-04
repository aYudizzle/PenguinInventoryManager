package dev.ayupi.pse_new.feature.itemmaster.di

import dev.ayupi.pse_new.feature.itemmaster.ItemMasterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val itemMasterModule = module {
    viewModelOf(::ItemMasterViewModel)
}