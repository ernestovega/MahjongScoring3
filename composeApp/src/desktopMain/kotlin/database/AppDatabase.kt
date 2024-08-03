package database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import screens.common.ui.APP_DATABASE_NAME
import java.io.File

fun getRoomDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
