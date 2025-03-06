package di

import data.database.sqldelight.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(context = get()) }
}