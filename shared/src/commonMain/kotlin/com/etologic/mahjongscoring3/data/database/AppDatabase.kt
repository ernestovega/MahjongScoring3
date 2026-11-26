package com.etologic.mahjongscoring3.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** Same file name as Mahjong Scoring 2, so updating the app keeps all the saved games. */
const val DATABASE_NAME = "MahjongScoring2"

@Database(entities = [DbGame::class, DbRound::class], version = 2)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
    abstract fun roundsDao(): RoundsDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

/** Exact copy of Mahjong Scoring 2 migration. */
object Migration1to2 : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        //Remove "Combinations" and "Tables" tables and "Rounds.roundDuration" column
        connection.execSQL("DROP TABLE IF EXISTS Combinations")
        connection.execSQL("DROP TABLE IF EXISTS Tables")
        // Replace "Rounds.isEnded" column by new "Games.endDate" column
        connection.execSQL("ALTER TABLE Games ADD COLUMN endDate INTEGER")
        connection.execSQL("UPDATE Games SET endDate = startDate")
        // Add "Games.gameName" column
        connection.execSQL("ALTER TABLE Games ADD COLUMN gameName TEXT NOT NULL DEFAULT ''")
        connection.execSQL("UPDATE Games SET gameName = ''")
        // Remove "Rounds.roundDuration", "Rounds.isEnded" and "Rounds.pointsPX" columns.
        // Make "Rounds.winnerInitialSeat" and "Rounds.discarderInitialSeat" columns nullable
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Rounds_temp` (`gameId` INTEGER NOT NULL, `roundId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `winnerInitialSeat` INTEGER, `discarderInitialSeat` INTEGER, `handPoints` INTEGER NOT NULL, `penaltyP1` INTEGER NOT NULL, `penaltyP2` INTEGER NOT NULL, `penaltyP3` INTEGER NOT NULL, `penaltyP4` INTEGER NOT NULL, FOREIGN KEY(`gameId`) REFERENCES `Games`(`gameId`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        connection.execSQL("INSERT INTO `Rounds_temp` SELECT `gameId`,`roundId`,`winnerInitialSeat`,`discarderInitialSeat`,`handPoints`,`penaltyP1`,`penaltyP2`,`penaltyP3`,`penaltyP4` FROM `Rounds`")
        connection.execSQL("DROP TABLE `Rounds`")
        connection.execSQL("ALTER TABLE `Rounds_temp` RENAME TO `Rounds`")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Rounds_gameId_roundId` ON `Rounds` (`gameId`, `roundId`)")
    }
}

fun buildAppDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    driver: SQLiteDriver = BundledSQLiteDriver(),
): AppDatabase =
    builder
        .addMigrations(Migration1to2)
        .setDriver(driver)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
