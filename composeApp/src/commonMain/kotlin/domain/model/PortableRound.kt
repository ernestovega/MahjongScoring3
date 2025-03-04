package domain.model

import data.database.room.tables.DbRound
import domain.model.enums.TableWinds
import kotlinx.serialization.Serializable
import ui.common.components.GameId
import ui.common.components.NOT_SET_ROUND_ID

@Serializable
data class PortableRound(
    val roundNumber: Int,
    val winnerInitialSeat: Int?,
    val discarderInitialSeat: Int?,
    val handPoints: Int,
    val pointsP1: Int,
    val pointsP2: Int,
    val pointsP3: Int,
    val pointsP4: Int,
    val penaltyP1: Int,
    val penaltyP2: Int,
    val penaltyP3: Int,
    val penaltyP4: Int,
)

fun UiRound.toPortableRound() = PortableRound(
    roundNumber = roundNumber,
    winnerInitialSeat = winnerInitialSeat?.index,
    discarderInitialSeat = discarderInitialSeat?.index,
    handPoints = handPoints,
    pointsP1 = pointsP1,
    pointsP2 = pointsP2,
    pointsP3 = pointsP3,
    pointsP4 = pointsP4,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4
)

fun List<PortableRound>.toDbRounds(gameId: GameId): List<DbRound> =
    this.map { it.toDbRound(gameId) }

private fun PortableRound.toDbRound(gameId: GameId): DbRound =
    DbRound(
        gameId = gameId,
        winnerInitialSeat = TableWinds.from(winnerInitialSeat),
        discarderInitialSeat = TableWinds.from(discarderInitialSeat),
        handPoints = handPoints,
        penaltyP1 = penaltyP1,
        penaltyP2 = penaltyP2,
        penaltyP3 = penaltyP3,
        penaltyP4 = penaltyP4,
    )