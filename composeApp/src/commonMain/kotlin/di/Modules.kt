package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import screens.help.diffs.DiffsScreenViewModel
import screens.help.fan.FanScreenViewModel
import screens.help.fan.repository.DefaultFanRepository
import screens.help.fan.repository.FanRepository
import screens.help.diffs.repository.DefaultDiffsRepository
import screens.help.diffs.repository.DiffsRepository

expect val platformModule: Module

val sharedModule = module {
    singleOf(::DefaultFanRepository).bind<FanRepository>()
    viewModelOf(::FanScreenViewModel)

    singleOf(::DefaultDiffsRepository).bind<DiffsRepository>()
    viewModelOf(::DiffsScreenViewModel)
}