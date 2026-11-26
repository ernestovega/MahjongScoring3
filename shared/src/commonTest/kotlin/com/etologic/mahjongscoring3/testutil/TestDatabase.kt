package com.etologic.mahjongscoring3.testutil

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import com.etologic.mahjongscoring3.data.database.AppDatabase

/**
 * Base class for tests that need the platform (Room, DataStore files).
 * On Android it runs the test with Robolectric (like MS2 data tests); on iOS it does nothing.
 */
expect abstract class PlatformTest()

/** A Room builder for a database file at [path] (an absolute path from [testFilePath]). */
expect fun testDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase>

/** SQLite driver used by the tests: the framework one on Android/Robolectric, the bundled one on iOS. */
expect fun testSqliteDriver(): SQLiteDriver

/** Absolute path of a fresh (deleted) file named [name] in a temporary directory. */
expect fun testFilePath(name: String): String
