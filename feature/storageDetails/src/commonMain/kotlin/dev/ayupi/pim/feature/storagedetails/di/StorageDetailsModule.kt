package dev.ayupi.pim.feature.storagedetails.di

import dev.ayupi.pim.feature.storagedetails.StorageDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val storageDetailsModule = module {
    viewModel<StorageDetailsViewModel> { (storageId: String) ->
        StorageDetailsViewModel(storageId, get(), get())
    }
}