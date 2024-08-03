package di

import com.etologic.mahjongscoring.database.getRoomDatabase
import database.AppDatabase
import database.daos.GamesDao
import database.daos.RoundsDao
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(get()) }
}