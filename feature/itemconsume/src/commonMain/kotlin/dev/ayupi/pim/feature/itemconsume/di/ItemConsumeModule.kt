package dev.ayupi.pim.feature.itemconsume.di

import dev.ayupi.pim.feature.itemconsume.ItemConsumeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val itemConsumeModule = module {
    viewModelOf(::ItemConsumeViewModel)
}
