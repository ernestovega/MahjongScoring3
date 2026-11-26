package com.etologic.mahjongscoring3.testutil

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.etologic.mahjongscoring3.data.database.AppDatabase
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory

actual abstract class PlatformTest actual constructor()

actual fun testDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(name = path)

actual fun testSqliteDriver(): SQLiteDriver = BundledSQLiteDriver()

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun testFilePath(name: String): String {
    val path = NSTemporaryDirectory().trimEnd('/') + "/ms3-tests-$name"
    listOf("", "-wal", "-shm", "-journal").forEach { NSFileManager.defaultManager.removeItemAtPath(path + it, null) }
    return path
}
