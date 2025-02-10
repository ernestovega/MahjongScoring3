package di

import data.database.AppDatabase
import data.database.app.getDatabaseBuilder
import data.database.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}