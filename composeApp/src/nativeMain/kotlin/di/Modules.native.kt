package di

import data.database.room.AppDatabase
import data.database.room.getDatabaseBuilder
import data.database.room.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}