package dev.ayupi.pse_new.feature.storageoverview.di

import dev.ayupi.pse_new.feature.storageoverview.StorageOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storageOverviewModule = module {
    viewModelOf(::StorageOverviewViewModel)
}