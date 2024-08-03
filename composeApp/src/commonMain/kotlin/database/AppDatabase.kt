package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import database.converters.DateConverter
import database.converters.TableWindsConverter
import database.daos.GamesDao
import database.daos.RoundsDao
import database.tables.DbGame
import database.tables.DbRound

@Database(
    entities = [DbGame::class, DbRound::class],
    version = 1,
)
@TypeConverters(DateConverter::class, TableWindsConverter::class)
abstract class AppDatabase : RoomDatabase(), DB {
    abstract val gamesDao: GamesDao
    abstract val roundsDao: RoundsDao
    override fun clearAllTables() {}
}

interface DB {
    fun clearAllTables() {}
}