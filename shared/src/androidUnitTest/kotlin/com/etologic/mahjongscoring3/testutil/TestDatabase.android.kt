package com.etologic.mahjongscoring3.testutil

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring3.data.database.AppDatabase
import org.junit.runner.RunWith
import java.io.File
import java.nio.file.Files

@RunWith(AndroidJUnit4::class)
actual abstract class PlatformTest actual constructor()

actual fun testDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(ApplicationProvider.getApplicationContext<Context>(), path)

// The bundled driver ships native libraries for Android devices only, not for the JVM running Robolectric.
actual fun testSqliteDriver(): SQLiteDriver = AndroidSQLiteDriver()

private val tempDir: File by lazy { Files.createTempDirectory("ms3-tests").toFile() }

actual fun testFilePath(name: String): String {
    val file = File(tempDir, name)
    listOf("", "-wal", "-shm", "-journal").forEach { File(file.path + it).delete() }
    return file.absolutePath
}
