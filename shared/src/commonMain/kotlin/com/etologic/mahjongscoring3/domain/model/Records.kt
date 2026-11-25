package com.etologic.mahjongscoring3.domain.model

typealias GameId = Long
typealias RoundId = Long

const val NOT_SET_GAME_ID: GameId = 0
const val NOT_SET_ROUND_ID: RoundId = 0

/** Persisted game row (same columns as Mahjong Scoring 2 "Games" table). Dates are epoch millis. */
data class GameRecord(
    val gameId: GameId = NOT_SET_GAME_ID,
    val nameP1: String = "",
    val nameP2: String = "",
    val nameP3: String = "",
    val nameP4: String = "",
    val startDate: Long = 0,
    val endDate: Long? = null,
    val gameName: String = "",
)

/** Persisted round row (same columns as Mahjong Scoring 2 "Rounds" table). */
data class RoundRecord(
    val gameId: GameId,
    val roundId: RoundId = NOT_SET_ROUND_ID,
    val winnerInitialSeat: TableWinds? = null,
    val discarderInitialSeat: TableWinds? = null,
    val handPoints: Int = 0,
    val penaltyP1: Int = 0,
    val penaltyP2: Int = 0,
    val penaltyP3: Int = 0,
    val penaltyP4: Int = 0,
) {
    fun isOngoing(): Boolean = winnerInitialSeat == null
}
