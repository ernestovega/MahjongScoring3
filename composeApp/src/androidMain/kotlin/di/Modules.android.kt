package di

import com.etologic.mahjongscoring.database.getDatabaseBuilder
import database.AppDatabase
import database.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get())) }
}