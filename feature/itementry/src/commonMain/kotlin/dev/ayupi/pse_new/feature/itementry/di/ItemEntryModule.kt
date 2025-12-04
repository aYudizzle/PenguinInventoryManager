package dev.ayupi.pse_new.feature.itementry.di

import dev.ayupi.pse_new.feature.itementry.ItemEntryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val itemEntryModule = module {
    viewModel<ItemEntryViewModel> { (itemId: String?) ->
        ItemEntryViewModel(itemId, get())
    }
}