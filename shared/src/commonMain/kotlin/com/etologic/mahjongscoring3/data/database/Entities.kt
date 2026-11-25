package com.etologic.mahjongscoring3.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.TableWinds

/*
 * IMPORTANT: these entities must produce exactly the same schema as Mahjong Scoring 2
 * (same table names, column names, column order, types, nullability, defaults and indices),
 * so the existing "MahjongScoring2" database keeps working after updating the app.
 */

@Entity(
    tableName = "Games",
    indices = [Index(value = ["gameId"], unique = true)],
)
data class DbGame(
    @PrimaryKey(autoGenerate = true) val gameId: Long = 0,
    val nameP1: String = "",
    val nameP2: String = "",
    val nameP3: String = "",
    val nameP4: String = "",
    val startDate: Long = 0,
    val endDate: Long? = null,
    @ColumnInfo(defaultValue = "") val gameName: String = "",
)

@Entity(
    tableName = "Rounds",
    foreignKeys = [ForeignKey(
        entity = DbGame::class,
        parentColumns = ["gameId"],
        childColumns = ["gameId"],
    )],
    indices = [Index(value = ["gameId", "roundId"], unique = true)],
)
data class DbRound(
    val gameId: Long,
    @PrimaryKey(autoGenerate = true) val roundId: Long = 0,
    /** TableWinds code (-1 NONE, 0 EAST, 1 SOUTH, 2 WEST, 3 NORTH) or null when the round is ongoing. */
    val winnerInitialSeat: Int? = null,
    val discarderInitialSeat: Int? = null,
    val handPoints: Int = 0,
    val penaltyP1: Int = 0,
    val penaltyP2: Int = 0,
    val penaltyP3: Int = 0,
    val penaltyP4: Int = 0,
)

fun DbGame.toRecord() = GameRecord(
    gameId = gameId,
    nameP1 = nameP1,
    nameP2 = nameP2,
    nameP3 = nameP3,
    nameP4 = nameP4,
    startDate = startDate,
    endDate = endDate,
    gameName = gameName,
)

fun GameRecord.toDb() = DbGame(
    gameId = gameId,
    nameP1 = nameP1,
    nameP2 = nameP2,
    nameP3 = nameP3,
    nameP4 = nameP4,
    startDate = startDate,
    endDate = endDate,
    gameName = gameName,
)

fun DbRound.toRecord() = RoundRecord(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = winnerInitialSeat?.let { TableWinds.from(it) },
    discarderInitialSeat = discarderInitialSeat?.let { TableWinds.from(it) },
    handPoints = handPoints,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4,
)

fun RoundRecord.toDb() = DbRound(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = winnerInitialSeat?.code,
    discarderInitialSeat = discarderInitialSeat?.code,
    handPoints = handPoints,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4,
)
