package dev.ayupi.pim.feature.storageoverview.di

import dev.ayupi.pim.feature.storageoverview.StorageOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storageOverviewModule = module {
    viewModelOf(::StorageOverviewViewModel)
}