package com.etologic.mahjongscoring

import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(context = get()) }
}