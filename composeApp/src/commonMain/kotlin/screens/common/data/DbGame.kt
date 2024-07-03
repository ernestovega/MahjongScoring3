package screens.common.data

//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.Index
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import screens.common.model.GameId
import screens.common.ui.NOT_SET_GAME_ID

//import com.etologic.mahjongscoring2.data_source.local_data_sources.room.converters.DateConverter

//@Entity(
//    tableName = "Games",
//    indices = [Index(
//        value = ["gameId"],
//        unique = true,
//    )],
//)
data class DbGame(
//    @field:PrimaryKey(autoGenerate = true)
    val gameId: GameId = NOT_SET_GAME_ID,
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
//    @TypeConverters(DateConverter::class)
    val startDate: Instant = Clock.System.now(),
//    @TypeConverters(DateConverter::class)
    var endDate: Instant? = null,
//    @ColumnInfo(defaultValue = "")
    var gameName: String = "",
)
