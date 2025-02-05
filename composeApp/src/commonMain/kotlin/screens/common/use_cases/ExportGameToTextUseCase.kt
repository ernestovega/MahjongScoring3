package screens.common.use_cases

import kotlinx.coroutines.flow.firstOrNull
import screens.common.model.UiGame
import screens.common.model.enums.TableWinds.NONE
import screens.common.model.exceptions.GameNotFoundException
import screens.common.model.exceptions.RankingDataGenerationException
import screens.common.ui.GameId
import screens.common.use_cases.utils.RankingTableHelper
import screens.common.use_cases.utils.prettifyOneLine
import screens.common.use_cases.utils.toSignedString

class ExportGameToTextUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<String> =
        runCatching {
            getOneGameFlowUseCase.invoke(gameId)
                .firstOrNull()
                ?.let { uiGame ->
                    val text = buildText(uiGame)
                    text
                }
                ?: throw GameNotFoundException(gameId)
        }

    private fun buildText(uiGame: UiGame): String =
        with(StringBuilder()) {
            buildFinalResultsText(uiGame)
//            buildRoundsText(uiGame, getStrRes)
            toString()
        }

    private fun StringBuilder.buildFinalResultsText(uiGame: UiGame) {
        RankingTableHelper.generateRankingTable(uiGame)?.let { rankingData ->
//            appendLine("- GAME:")
//            appendLine()
            appendLine(uiGame.gameName.ifBlank { uiGame.startDate.prettifyOneLine() })
            appendLine()
//            appendLine("- FINAL RESULTS:")
//            appendLine()
            appendLine("1st:    ${rankingData.sortedPlayersRankings[0].toSignedString()}")
            appendLine("2nd:    ${rankingData.sortedPlayersRankings[1].toSignedString()}")
            appendLine("3rd:    ${rankingData.sortedPlayersRankings[2].toSignedString()}")
            appendLine("4th:    ${rankingData.sortedPlayersRankings[3].toSignedString()}")
            appendLine()
            if (uiGame.getBestHand().playerInitialPosition != NONE) {
                append("Best hand:    ${uiGame.getBestHand().handValue}  -  ${uiGame.getBestHand().playerName}")
                appendLine()
            }
        }
            ?: throw RankingDataGenerationException()
    }

//    private fun StringBuilder.buildRoundsText(uiGame: UiGame, getStrRes: (Int) -> String) {
//        val initialEastPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(EAST))
//        val initialSouthPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(SOUTH))
//        val initialWestPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(WEST))
//        val initialNorthPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(NORTH))
//
//        appendLine()
//        appendLine("- ${getStrRes(R.string.rounds).uppercase()}:")
//        appendLine("(${getStrRes(R.string.rounds_dont_count_penalties)})")
//        appendLine()
//        uiGame.uiRounds.forEach { round ->
//            if (round.winnerInitialSeat != null || round.areTherePenalties()) {
//                append(round.roundNumber.toString().padStart(2))
//                appendLine()
//                append("${getStrRes(R.string.winner_)} ")
//                append(
//                    when (round.winnerInitialSeat) {
//                        null -> "-"
//                        NONE -> getStrRes(R.string.draw)
//                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.winnerInitialSeat!!))
//                    }
//                )
//                appendLine()
//                append("${getStrRes(R.string.points_)} ${round.dbRound.handPoints}")
//                appendLine()
//                append("${getStrRes(R.string.from)}: ")
//                append(
//                    when (round.discarderInitialSeat) {
//                        null -> "-"
//                        NONE -> when (round.dbRound.winnerInitialSeat) {
//                            null -> "-"
//                            NONE -> getStrRes(R.string.draw)
//                            else -> getStrRes(R.string.self_pick)
//                        }
//
//                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.dbRound.discarderInitialSeat!!))
//                    }
//                )
//                appendLine()
//                if (round.areTherePenalties()) {
//                    appendLine()
//                    appendLine("${getStrRes(R.string.penalties)}: ")
//                    appendLine("$initialEastPlayerName: ${round.penaltyP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.penaltyP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.penaltyP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.penaltyP4.toSignedString()}")
//                }
//                if (round.discarderInitialSeat != null && round.discarderInitialSeat != NONE) {
//                    appendLine()
//                    appendLine("${getStrRes(R.string.partials)}: ")
//                    appendLine("$initialEastPlayerName: ${round.pointsP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.pointsP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.pointsP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.pointsP4.toSignedString()}")
//                    appendLine()
//                    appendLine("${getStrRes(R.string.totals)}: ")
//                    appendLine("$initialEastPlayerName: ${round.totalPointsP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.totalPointsP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.totalPointsP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.totalPointsP4.toSignedString()}")
//                    appendLine()
//                }
//                appendLine()
//            }
//        }
//    }
}
