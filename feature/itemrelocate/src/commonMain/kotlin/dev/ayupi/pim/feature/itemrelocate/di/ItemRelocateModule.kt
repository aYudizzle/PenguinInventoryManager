package dev.ayupi.pim.feature.itemrelocate.di

import dev.ayupi.pim.feature.itemrelocate.ItemRelocateViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val itemRelocateModule = module {
    viewModelOf(::ItemRelocateViewModel)
}
