package com.etologic.mahjongscoring3.domain.model

data class BestHand(
    val roundNumber: Int = 0,
    val handValue: Int = 0,
    val playerInitialPosition: TableWinds = TableWinds.NONE,
    val playerName: String = "",
)

data class HuData(
    val points: Int,
    val winnerInitialSeat: TableWinds,
    val discarderInitialSeat: TableWinds = TableWinds.NONE,
)

data class PenaltyData(
    val points: Int,
    val isDivided: Boolean,
    val penalizedPlayerInitialSeat: TableWinds,
)

data class PlayerRanking(
    val name: String,
    val score: Int,
    val points: String = "",
)

data class RankingData(
    val sortedPlayersRankings: List<PlayerRanking>,
    val bestHandPlayerPoints: String,
    val bestHandPlayerName: String,
    val numRounds: Int,
    val sNumRounds: String,
)
