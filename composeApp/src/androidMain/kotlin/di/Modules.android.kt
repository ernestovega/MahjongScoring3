package di

import com.etologic.mahjongscoring.data.database.getDatabaseBuilder
import data.database.AppDatabase
import data.database.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    //Database
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get())) }
}