package database.app

import androidx.room.Room
import androidx.room.RoomDatabase
import database.AppDatabase
import screens.common.ui.APP_DATABASE_NAME
import java.io.File

internal fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}