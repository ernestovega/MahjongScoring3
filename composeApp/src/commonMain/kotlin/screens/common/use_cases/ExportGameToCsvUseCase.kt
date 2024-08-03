package screens.common.use_cases

import androidx.annotation.VisibleForTesting
//import screens.common.model.GameId
import screens.common.model.UiGame
import screens.common.model.UiRound
import screens.common.model.enums.TableWinds.EAST
import screens.common.model.enums.TableWinds.NONE
import screens.common.model.enums.TableWinds.NORTH
import screens.common.model.enums.TableWinds.SOUTH
import screens.common.model.enums.TableWinds.WEST
//import screens.common.model.exceptions.GameNotFoundException
//import kotlinx.coroutines.flow.firstOrNull
import screens.common.use_cases.utils.normalizeName
//import screens.common.use_cases.utils.toFileNameFormat
//import screens.common.use_cases.utils.writeToCsvFile
//import java.io.File

class ExportGameToCsvUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
//    suspend operator fun invoke(gameId: GameId, directory: File?): Result<File> =
//        runCatching {
//            getOneGameFlowUseCase.invoke(gameId)
//                .firstOrNull()
//                ?.let { uiGame ->
//                    val fileText = buildCsvText(uiGame)
//                    val fileName = normalizeName(uiGame.gameName)
//                        .replace(" ", "_")
//                        .ifEmpty { uiGame.startDate.toFileNameFormat() }
//                        .plus(".csv")
//                    val csvFile = writeToCsvFile(fileName, fileText, directory)
//                    csvFile
//                }
//                ?: throw GameNotFoundException(gameId)
//        }

    @VisibleForTesting
    fun buildCsvText(uiGame: UiGame): String =
        with(StringBuilder()) {
            buildHeader(uiGame)
            buildRows(uiGame)
            toString()
        }

    private fun StringBuilder.buildHeader(uiGame: UiGame) {
        val initialEastPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(EAST))
        val initialSouthPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(SOUTH))
        val initialWestPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(WEST))
        val initialNorthPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(NORTH))

        append("Round,")
        append("Winner,")
        append("Discarder,")
        append("Hand Points,")
        append("Points $initialEastPlayerName,")
        append("Points $initialSouthPlayerName,")
        append("Points $initialWestPlayerName,")
        append("Points $initialNorthPlayerName,")
        append("Penalty $initialEastPlayerName,")
        append("Penalty $initialSouthPlayerName,")
        append("Penalty $initialWestPlayerName,")
        append("Penalty $initialNorthPlayerName,")
        append("Game name,")
        append("Game start date,")
        append("Game end date")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGame: UiGame) {
        uiGame.uiRounds.forEach { uiRound ->
            buildRoundDataRow(uiRound, uiGame)
            buildRoundTotalsRow(uiRound)
        }
    }

    private fun StringBuilder.buildRoundDataRow(uiRound: UiRound, uiGame: UiGame) {
        append("${uiRound.roundNumber},") // 1
        append(
            when (uiRound.winnerInitialSeat) {
                null,
                NONE -> "-"

                else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.winnerInitialSeat))
            }
        ).also { append(",") } // 2
        append(
            when (uiRound.discarderInitialSeat) {
                null,
                NONE -> "-"

                else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.discarderInitialSeat))
            }
        ).also { append(",") } // 3
        append("${uiRound.handPoints},") // 4
        append("${uiRound.pointsP1},") // 5
        append("${uiRound.pointsP2},") // 6
        append("${uiRound.pointsP3},") // 7
        append("${uiRound.pointsP4},") // 8
        append("${uiRound.penaltyP1},") // 9
        append("${uiRound.penaltyP2},") // 10
        append("${uiRound.penaltyP3},") // 11
        append("${uiRound.penaltyP4},") // 12
        append("${uiGame.gameName},") // 13
        append("${uiGame.startDate},") // 14
        append(uiGame.endDate) // 15
        appendLine()
    }

    private fun StringBuilder.buildRoundTotalsRow(
        uiRound: UiRound,
    ) {
        append("-,") // 1
        append("-,") // 2
        append("-,") // 3
        append("-,") // 4
        append("${uiRound.totalPointsP1},") // 5
        append("${uiRound.totalPointsP2},") // 6
        append("${uiRound.totalPointsP3},") // 7
        append("${uiRound.totalPointsP4},") // 8
        append("-,") // 9
        append("-,") // 10
        append("-,") // 11
        append("-,") // 12
        append("-,") // 13
        append("-,") // 14
        append("-") // 15
        appendLine()
    }
}