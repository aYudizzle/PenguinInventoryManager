package dev.ayupi.pse_new.feature.settings.di

import dev.ayupi.pse_new.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}