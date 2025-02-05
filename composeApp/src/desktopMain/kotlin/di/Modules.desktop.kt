package di

import database.AppDatabase
import database.app.getDatabaseBuilder
import database.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}