package data.database.app

import androidx.room.Room
import androidx.room.RoomDatabase
import data.database.AppDatabase
import ui.common.components.APP_DATABASE_NAME
import java.io.File

internal fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}