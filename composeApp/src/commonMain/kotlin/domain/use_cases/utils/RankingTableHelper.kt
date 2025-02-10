package domain.use_cases.utils

import domain.model.PlayerRanking
import domain.model.RankingData
import domain.model.UiGame

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
            sortedPlayersRankings,
            if (bestHand.handValue == 0) "-" else bestHand.handValue.toString(),
            if (bestHand.handValue == 0) "-" else bestHand.playerName,
            uiGame.uiRounds.size,
            uiGame.uiRounds.size.toString()
        )
    }

    private fun getSortedPlayersRankings(uiGame: UiGame): List<PlayerRanking> {
        var playersRankings = setPlayersNamesAndScores(uiGame)
        playersRankings = playersRankings.sortedByDescending { it.score }
        setPlayersTablePoints(playersRankings)
        return playersRankings
    }

    private fun setPlayersNamesAndScores(uiGame: UiGame): List<PlayerRanking> = listOf(
        PlayerRanking(uiGame.nameP1, uiGame.ongoingOrLastRound.totalPointsP1),
        PlayerRanking(uiGame.nameP2, uiGame.ongoingOrLastRound.totalPointsP2),
        PlayerRanking(uiGame.nameP3, uiGame.ongoingOrLastRound.totalPointsP3),
        PlayerRanking(uiGame.nameP4, uiGame.ongoingOrLastRound.totalPointsP4)
    )

    private fun setPlayersTablePoints(sortedPlayers: List<PlayerRanking>): List<PlayerRanking> {
        sortedPlayers[0].points = FIRST_POSITION_POINTS
        sortedPlayers[1].points = SECOND_POSITION_POINTS
        sortedPlayers[2].points = THIRD_POSITION_POINTS
        sortedPlayers[3].points = FOURTH_POSITION_POINTS
        val scorePlayerFirst = sortedPlayers[0].score
        val scorePlayerSecond = sortedPlayers[1].score
        val scorePlayerThird = sortedPlayers[2].score
        val scorePlayerFourth = sortedPlayers[3].score

        if (scorePlayerFirst == scorePlayerSecond &&
            scorePlayerSecond == scorePlayerThird &&
            scorePlayerThird == scorePlayerFourth
        ) {
            sortedPlayers[0].points = DRAW_4
            sortedPlayers[1].points = DRAW_4
            sortedPlayers[2].points = DRAW_4
            sortedPlayers[3].points = DRAW_4
            return sortedPlayers
        } else if (scorePlayerFirst == scorePlayerSecond && scorePlayerSecond == scorePlayerThird) {
            sortedPlayers[0].points = DRAW_FIRST_3
            sortedPlayers[1].points = DRAW_FIRST_3
            sortedPlayers[2].points = DRAW_FIRST_3
            return sortedPlayers
        } else if (scorePlayerSecond == scorePlayerThird && scorePlayerThird == scorePlayerFourth) {
            sortedPlayers[1].points = DRAW_LAST_3
            sortedPlayers[2].points = DRAW_LAST_3
            sortedPlayers[3].points = DRAW_LAST_3
            return sortedPlayers
        } else {
            if (scorePlayerFirst == scorePlayerSecond) {
                sortedPlayers[0].points = DRAW_FIRST_2
                sortedPlayers[1].points = DRAW_FIRST_2
            }
            if (scorePlayerSecond == scorePlayerThird) {
                sortedPlayers[1].points = DRAW_SECOND_2
                sortedPlayers[2].points = DRAW_SECOND_2
            }
            if (scorePlayerThird == scorePlayerFourth) {
                sortedPlayers[2].points = DRAW_LAST_2
                sortedPlayers[3].points = DRAW_LAST_2
            }
        }
        return sortedPlayers
    }
}
