package com.etologic.mahjongscoring3.domain.model

/** Final table points (4-2-1-0 with ties shared), ported from Mahjong Scoring 2. */
object RankingTableHelper {
    private const val FIRST_POSITION_POINTS = "4"
    private const val SECOND_POSITION_POINTS = "2"
    private const val THIRD_POSITION_POINTS = "1"
    private const val FOURTH_POSITION_POINTS = "0"
    private const val DRAW_4 = "1.75"
    private const val DRAW_FIRST_3 = "2.33"
    private const val DRAW_LAST_3 = "1"
    private const val DRAW_FIRST_2 = "3"
    private const val DRAW_SECOND_2 = "1.5"
    private const val DRAW_LAST_2 = "0.5"

    fun generateRankingTable(uiGame: UiGame?): RankingData? {
        if (uiGame == null) return null
        val sortedPlayersRankings = getSortedPlayersRankings(uiGame)
        val bestHand = uiGame.getBestHand()
        return RankingData(
            sortedPlayersRankings = sortedPlayersRankings,
            bestHandPlayerPoints = if (bestHand.handValue == 0) "-" else bestHand.handValue.toString(),
            bestHandPlayerName = if (bestHand.handValue == 0) "-" else bestHand.playerName,
            numRounds = uiGame.uiRounds.size,
            sNumRounds = uiGame.uiRounds.size.toString(),
        )
    }

    private fun getSortedPlayersRankings(uiGame: UiGame): List<PlayerRanking> {
        val sorted = listOf(
            PlayerRanking(uiGame.nameP1, uiGame.ongoingRound.totalPointsP1),
            PlayerRanking(uiGame.nameP2, uiGame.ongoingRound.totalPointsP2),
            PlayerRanking(uiGame.nameP3, uiGame.ongoingRound.totalPointsP3),
            PlayerRanking(uiGame.nameP4, uiGame.ongoingRound.totalPointsP4),
        ).sortedByDescending { it.score }
        val points = getPlayersTablePoints(sorted.map { it.score })
        return sorted.mapIndexed { index, playerRanking -> playerRanking.copy(points = points[index]) }
    }

    private fun getPlayersTablePoints(scores: List<Int>): List<String> {
        val points = mutableListOf(
            FIRST_POSITION_POINTS,
            SECOND_POSITION_POINTS,
            THIRD_POSITION_POINTS,
            FOURTH_POSITION_POINTS,
        )
        val (first, second, third, fourth) = scores
        if (first == second && second == third && third == fourth) {
            points[0] = DRAW_4
            points[1] = DRAW_4
            points[2] = DRAW_4
            points[3] = DRAW_4
        } else if (first == second && second == third) {
            points[0] = DRAW_FIRST_3
            points[1] = DRAW_FIRST_3
            points[2] = DRAW_FIRST_3
        } else if (second == third && third == fourth) {
            points[1] = DRAW_LAST_3
            points[2] = DRAW_LAST_3
            points[3] = DRAW_LAST_3
        } else {
            if (first == second) {
                points[0] = DRAW_FIRST_2
                points[1] = DRAW_FIRST_2
            }
            if (second == third) {
                points[1] = DRAW_SECOND_2
                points[2] = DRAW_SECOND_2
            }
            if (third == fourth) {
                points[2] = DRAW_LAST_2
                points[3] = DRAW_LAST_2
            }
        }
        return points
    }
}
