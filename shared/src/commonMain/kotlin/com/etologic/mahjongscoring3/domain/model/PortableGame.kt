package com.etologic.mahjongscoring3.domain.model

import kotlinx.serialization.Serializable

/** JSON format shared with Mahjong Scoring 2 exports. Do not rename fields. */
@Serializable
data class PortableGame(
    val gameName: String,
    val startDate: Long,
    val endDate: Long?,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
    val rounds: List<PortableRound> = emptyList(),
)

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

fun UiGame.toPortableGame() = PortableGame(
    gameName = gameName,
    startDate = startDate,
    endDate = endDate,
    nameP1 = nameP1,
    nameP2 = nameP2,
    nameP3 = nameP3,
    nameP4 = nameP4,
    rounds = uiRounds.map { it.toPortableRound() },
)

fun UiRound.toPortableRound() = PortableRound(
    roundNumber = roundNumber,
    winnerInitialSeat = winnerInitialSeat?.code,
    discarderInitialSeat = discarderInitialSeat?.code,
    handPoints = handPoints,
    pointsP1 = pointsP1,
    pointsP2 = pointsP2,
    pointsP3 = pointsP3,
    pointsP4 = pointsP4,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4,
)

fun PortableGame.toGameRecord(gameId: GameId): GameRecord =
    GameRecord(
        gameId = gameId,
        nameP1 = nameP1,
        nameP2 = nameP2,
        nameP3 = nameP3,
        nameP4 = nameP4,
        startDate = startDate,
        endDate = endDate,
        gameName = gameName,
    )

fun List<PortableRound>.toRoundRecords(gameId: GameId): List<RoundRecord> =
    map { it.toRoundRecord(gameId) }

private fun PortableRound.toRoundRecord(gameId: GameId): RoundRecord =
    RoundRecord(
        gameId = gameId,
        winnerInitialSeat = TableWinds.from(winnerInitialSeat),
        discarderInitialSeat = TableWinds.from(discarderInitialSeat),
        handPoints = handPoints,
        penaltyP1 = penaltyP1,
        penaltyP2 = penaltyP2,
        penaltyP3 = penaltyP3,
        penaltyP4 = penaltyP4,
    )
