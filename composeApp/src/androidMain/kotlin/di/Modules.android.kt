package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import screens.help.fan.FanScreenViewModel

actual val platformModule = module {
    viewModelOf(::FanScreenViewModel)
}