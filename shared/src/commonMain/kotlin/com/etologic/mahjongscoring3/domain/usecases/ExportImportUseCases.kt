package com.etologic.mahjongscoring3.domain.usecases

import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameNotFoundException
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.GamesNotFoundException
import com.etologic.mahjongscoring3.domain.model.JsonGamesNotValidException
import com.etologic.mahjongscoring3.domain.model.PlayerRanking
import com.etologic.mahjongscoring3.domain.model.PortableGame
import com.etologic.mahjongscoring3.domain.model.RankingDataGenerationException
import com.etologic.mahjongscoring3.domain.model.RankingTableHelper
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiRound
import com.etologic.mahjongscoring3.domain.model.toGameRecord
import com.etologic.mahjongscoring3.domain.model.toPortableGame
import com.etologic.mahjongscoring3.domain.model.toRoundRecords
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import com.etologic.mahjongscoring3.domain.utils.formatSigned
import com.etologic.mahjongscoring3.domain.utils.normalizeName
import com.etologic.mahjongscoring3.domain.utils.prettifyOneLine
import com.etologic.mahjongscoring3.domain.utils.sanitizeForFileName
import com.etologic.mahjongscoring3.domain.utils.toCsvDateFormat
import com.etologic.mahjongscoring3.domain.utils.toFileNameFormat
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** A file to be written and shared by the platform. */
data class ExportedFile(
    val fileName: String,
    val mimeType: String,
    val content: String,
)


private fun UiGame.exportFileName(extension: String): String =
    gameName.ifEmpty { startDate.toFileNameFormat() }
        .sanitizeForFileName()
        .plus(".$extension")

class ExportGameToTextUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<String> =
        runCatching {
            getOneGameFlowUseCase(gameId).firstOrNull()
                ?.let { uiGame -> buildText(uiGame) }
                ?: throw GameNotFoundException(gameId)
        }

    fun buildText(uiGame: UiGame): String =
        buildString {
            val rankingData = RankingTableHelper.generateRankingTable(uiGame)
                ?: throw RankingDataGenerationException()
            appendLine(uiGame.gameName.ifBlank { uiGame.startDate.prettifyOneLine() })
            appendLine()
            appendLine("1st:    ${rankingData.sortedPlayersRankings[0].toSignedString()}")
            appendLine("2nd:    ${rankingData.sortedPlayersRankings[1].toSignedString()}")
            appendLine("3rd:    ${rankingData.sortedPlayersRankings[2].toSignedString()}")
            appendLine("4th:    ${rankingData.sortedPlayersRankings[3].toSignedString()}")
            appendLine()
            val bestHand = uiGame.getBestHand()
            if (bestHand.playerInitialPosition != NONE) {
                append("Best hand:    ${bestHand.handValue}  -  ${bestHand.playerName}")
                appendLine()
            }
        }

    private fun PlayerRanking.toSignedString() = "$name    $points    (${score.formatSigned()})"
}

class ExportGameToCsvUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<ExportedFile> =
        runCatching {
            getOneGameFlowUseCase(gameId).firstOrNull()
                ?.let { uiGame ->
                    ExportedFile(
                        fileName = uiGame.exportFileName("csv"),
                        mimeType = "text/csv",
                        content = buildCsvText(uiGame),
                    )
                }
                ?: throw GameNotFoundException(gameId)
        }

    fun buildCsvText(uiGame: UiGame): String =
        buildString {
            buildHeader(uiGame)
            uiGame.uiRounds.forEach { uiRound ->
                buildRoundDataRow(uiRound, uiGame)
                buildRoundTotalsRow(uiRound)
            }
        }

    private fun StringBuilder.buildHeader(uiGame: UiGame) {
        val east = normalizeName(uiGame.getPlayerNameByInitialPosition(EAST))
        val south = normalizeName(uiGame.getPlayerNameByInitialPosition(SOUTH))
        val west = normalizeName(uiGame.getPlayerNameByInitialPosition(WEST))
        val north = normalizeName(uiGame.getPlayerNameByInitialPosition(NORTH))
        append("Round,")
        append("Winner,")
        append("Discarder,")
        append("Hand Points,")
        append("Points $east,")
        append("Points $south,")
        append("Points $west,")
        append("Points $north,")
        append("Penalty $east,")
        append("Penalty $south,")
        append("Penalty $west,")
        append("Penalty $north,")
        append("Game name,")
        append("Game start date,")
        append("Game end date")
        appendLine()
    }

    private fun StringBuilder.buildRoundDataRow(uiRound: UiRound, uiGame: UiGame) {
        append("${uiRound.roundNumber},")
        append(
            when (val winner = uiRound.winnerInitialSeat) {
                null, NONE -> "-"
                else -> normalizeName(uiGame.getPlayerNameByInitialPosition(winner))
            }
        ).append(",")
        append(
            when (val discarder = uiRound.discarderInitialSeat) {
                null, NONE -> "-"
                else -> normalizeName(uiGame.getPlayerNameByInitialPosition(discarder))
            }
        ).append(",")
        append("${uiRound.handPoints},")
        append("${uiRound.pointsP1},")
        append("${uiRound.pointsP2},")
        append("${uiRound.pointsP3},")
        append("${uiRound.pointsP4},")
        append("${uiRound.penaltyP1},")
        append("${uiRound.penaltyP2},")
        append("${uiRound.penaltyP3},")
        append("${uiRound.penaltyP4},")
        append("${uiGame.gameName},")
        append("${uiGame.startDate.toCsvDateFormat()},")
        append(uiGame.endDate?.toCsvDateFormat() ?: "null")
        appendLine()
    }

    private fun StringBuilder.buildRoundTotalsRow(uiRound: UiRound) {
        append("-,-,-,-,")
        append("${uiRound.totalPointsP1},")
        append("${uiRound.totalPointsP2},")
        append("${uiRound.totalPointsP3},")
        append("${uiRound.totalPointsP4},")
        append("-,-,-,-,-,-,-")
        appendLine()
    }
}

class ExportGameToJsonUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<ExportedFile> =
        runCatching {
            getOneGameFlowUseCase(gameId).firstOrNull()
                ?.let { uiGame ->
                    ExportedFile(
                        fileName = uiGame.exportFileName("json"),
                        mimeType = "application/json",
                        content = jsonFrom(listOf(uiGame)),
                    )
                }
                ?: throw GameNotFoundException(gameId)
        }
}

class ExportGamesToJsonUseCase(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
) {
    suspend operator fun invoke(): Result<ExportedFile> =
        runCatching {
            val uiGames = getAllGamesFlowUseCase().firstOrNull() ?: throw GamesNotFoundException()
            ExportedFile(
                fileName = "MahjongMadrid2_DataBase.json",
                mimeType = "application/json",
                content = jsonFrom(uiGames),
            )
        }
}

fun jsonFrom(uiGames: List<UiGame>): String =
    Json.encodeToString(
        serializer = ListSerializer(PortableGame.serializer()),
        value = uiGames.map { it.toPortableGame() },
    )

class ImportGamesFromJsonUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val deleteGameUseCase: DeleteGameUseCase,
) {
    suspend operator fun invoke(jsonText: String): Result<List<GameId>> =
        runCatching {
            val portableGames = try {
                // Strict, as Mahjong Scoring 2: unknown keys make the file invalid
                Json.decodeFromString(ListSerializer(PortableGame.serializer()), jsonText)
            } catch (e: Exception) {
                throw JsonGamesNotValidException(e)
            }
            portableGames.map { portableGame -> createGameInDb(portableGame) }
        }

    private suspend fun createGameInDb(portableGame: PortableGame): GameId {
        val gameId = gamesRepository.insertOne(GameRecord()).getOrThrow()
        try {
            gamesRepository.updateOne(portableGame.toGameRecord(gameId)).getOrThrow()
            portableGame.rounds.toRoundRecords(gameId).forEach { roundRecord ->
                roundsRepository.insertOne(roundRecord).getOrThrow()
            }
        } catch (e: Exception) {
            deleteGameUseCase(gameId)
            throw e
        }
        return gameId
    }
}
