package dev.ayupi.pse_new.di

import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ayupi.pse_new.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
}