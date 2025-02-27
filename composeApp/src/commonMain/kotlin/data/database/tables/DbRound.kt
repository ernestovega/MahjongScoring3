package data.database.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import data.database.converters.TableWindsConverter
import domain.model.enums.TableWinds
import ui.common.components.GameId
import ui.common.components.NOT_SET_ROUND_ID
import ui.common.components.RoundId

@Entity(
    tableName = "Rounds",
    foreignKeys = [ForeignKey(
        entity = DbGame::class,
        parentColumns = ["gameId"],
        childColumns = ["gameId"],
    )],
    indices = [Index(
        value = ["gameId", "roundId"],
        unique = true,
    )],
)
data class DbRound(
    val gameId: GameId,
    @PrimaryKey(autoGenerate = true) val roundId: RoundId,
) {
    @TypeConverters(TableWindsConverter::class) var winnerInitialSeat: TableWinds? = null
    @TypeConverters(TableWindsConverter::class) var discarderInitialSeat: TableWinds? = null
    var handPoints: Int = 0
    var penaltyP1: Int = 0
    var penaltyP2: Int = 0
    var penaltyP3: Int = 0
    var penaltyP4: Int = 0

    constructor(
        gameId: GameId,
        roundId: RoundId = NOT_SET_ROUND_ID,
        winnerInitialSeat: TableWinds?,
        discarderInitialSeat: TableWinds?,
        handPoints: Int = 0,
        penaltyP1: Int = 0,
        penaltyP2: Int = 0,
        penaltyP3: Int = 0,
        penaltyP4: Int = 0,
    ) : this(gameId, roundId) {
        this.winnerInitialSeat = winnerInitialSeat
        this.discarderInitialSeat = discarderInitialSeat
        this.handPoints = handPoints
        this.penaltyP1 = penaltyP1
        this.penaltyP2 = penaltyP2
        this.penaltyP3 = penaltyP3
        this.penaltyP4 = penaltyP4
    }

    fun isOngoing(): Boolean = winnerInitialSeat == null
}