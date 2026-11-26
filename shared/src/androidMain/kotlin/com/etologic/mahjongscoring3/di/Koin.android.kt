package com.etologic.mahjongscoring3.di

import android.content.Context
import androidx.room.Room
import com.etologic.mahjongscoring3.data.database.AppDatabase
import com.etologic.mahjongscoring3.data.database.DATABASE_NAME
import com.etologic.mahjongscoring3.data.database.buildAppDatabase
import com.etologic.mahjongscoring3.data.settings.DATASTORE_FILE_NAME
import com.etologic.mahjongscoring3.data.settings.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

/** Same locations as Mahjong Scoring 2, so an update keeps the user's games and settings. */
actual val platformModule: Module = module {
    single<AppDatabase> {
        val context = get<Context>()
        buildAppDatabase(
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = context.getDatabasePath(DATABASE_NAME).absolutePath,
            )
        )
    }
    single {
        val context = get<Context>()
        createDataStore { context.filesDir.resolve("datastore/$DATASTORE_FILE_NAME").absolutePath }
    }
}
