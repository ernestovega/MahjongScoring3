package com.etologic.mahjongscoring3.domain.model

/**
 * A round of a game. Constructor values come from the database; the rest are calculated by [UiGame].
 */
data class UiRound(
    val gameId: GameId,
    val roundId: RoundId,
    val winnerInitialSeat: TableWinds?,
    val discarderInitialSeat: TableWinds?,
    val handPoints: Int,
    val penaltyP1: Int,
    val penaltyP2: Int,
    val penaltyP3: Int,
    val penaltyP4: Int,
) {
    var roundNumber: Int = 0
        internal set
    var pointsP1: Int = 0
        internal set
    var pointsP2: Int = 0
        internal set
    var pointsP3: Int = 0
        internal set
    var pointsP4: Int = 0
        internal set
    var totalPointsP1: Int = 0
        internal set
    var totalPointsP2: Int = 0
        internal set
    var totalPointsP3: Int = 0
        internal set
    var totalPointsP4: Int = 0
        internal set
    var isBestHand: Boolean = false
        internal set

    fun areTherePenalties(): Boolean =
        penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0

    fun isOngoing(): Boolean = winnerInitialSeat == null

    val roundPoints: List<Int> get() = listOf(pointsP1, pointsP2, pointsP3, pointsP4)
    val totalPoints: List<Int> get() = listOf(totalPointsP1, totalPointsP2, totalPointsP3, totalPointsP4)
    val penalties: List<Int> get() = listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)

    companion object {
        fun empty(gameId: GameId = NOT_SET_GAME_ID): UiRound =
            UiRound(
                gameId = gameId,
                roundId = NOT_SET_ROUND_ID,
                winnerInitialSeat = null,
                discarderInitialSeat = null,
                handPoints = 0,
                penaltyP1 = 0,
                penaltyP2 = 0,
                penaltyP3 = 0,
                penaltyP4 = 0,
            )
    }
}
