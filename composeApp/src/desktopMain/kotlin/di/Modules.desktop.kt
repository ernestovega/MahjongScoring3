package di

import database.AppDatabase
import database.daos.GamesDao
import database.daos.RoundsDao
import database.getRoomDatabase
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase() }
}