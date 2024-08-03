package database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import database.converters.DateConverter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import screens.common.ui.GameId
import screens.common.ui.NOT_SET_GAME_ID

@Entity(
    tableName = "Games",
    indices = [Index(
        value = ["gameId"],
        unique = true,
    )],
)
data class DbGame(
    @PrimaryKey(autoGenerate = true) val gameId: GameId = NOT_SET_GAME_ID,
    @ColumnInfo(defaultValue = "") var gameName: String = "",
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
    @TypeConverters(DateConverter::class) val startDate: Instant = Clock.System.now(),
    @TypeConverters(DateConverter::class) var endDate: Instant? = null,
)
