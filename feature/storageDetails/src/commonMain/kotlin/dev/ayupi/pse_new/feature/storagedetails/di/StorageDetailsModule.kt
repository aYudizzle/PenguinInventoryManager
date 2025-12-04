package dev.ayupi.pse_new.feature.storagedetails.di

import dev.ayupi.pse_new.feature.storagedetails.StorageDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storageDetailsModule = module {
    viewModel<StorageDetailsViewModel> { (storageId: String) ->
        StorageDetailsViewModel(storageId, get(), get())
    }
}