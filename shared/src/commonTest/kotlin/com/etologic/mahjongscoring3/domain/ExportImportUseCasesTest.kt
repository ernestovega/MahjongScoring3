package com.etologic.mahjongscoring3.domain

import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.toUiGame
import com.etologic.mahjongscoring3.domain.usecases.DeleteGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToTextUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.jsonFrom
import com.etologic.mahjongscoring3.domain.utils.toCsvDateFormat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Port of MS2 ExportImportGamesUseCasesTests, using in-memory fake repositories instead of Room. */
class ExportImportUseCasesTest {

    private val gamesRepository = FakeGamesRepository()
    private val roundsRepository = FakeRoundsRepository()
    private val getOneGameFlowUseCase = GetOneGameFlowUseCase(gamesRepository, roundsRepository)
    private val exportGameToTextUseCase = ExportGameToTextUseCase(getOneGameFlowUseCase)
    private val exportGameToCsvUseCase = ExportGameToCsvUseCase(getOneGameFlowUseCase)
    private val exportGameToJsonUseCase = ExportGameToJsonUseCase(getOneGameFlowUseCase)
    private val importGamesFromJsonUseCase = ImportGamesFromJsonUseCase(
        gamesRepository, roundsRepository, DeleteGameUseCase(gamesRepository, roundsRepository)
    )

    private var nextStartDate = 1_700_000_000_000L

    @Test
    fun exportGameResultsToTextUseCase() = runTest {
        val game = createCompleteGame()
        val exportedText = exportGameToTextUseCase(game.gameId).getOrThrow()
        val expectedText = "Test Game Name 1\n\n" +
            "1st:    Test Player 4    4    (+145)\n" +
            "2nd:    Test Player 3    2    (-3)\n" +
            "3rd:    Test Player 2    1    (-29)\n" +
            "4th:    Test Player 1    0    (-143)\n\n" +
            "Best hand:    28  -  Test Player 2\n"
        assertEquals(expectedText, exportedText)
    }

    @Test
    fun exportGameToCsvUseCase() = runTest {
        val uiGame = createCompleteGame()
        val s = uiGame.startDate.toCsvDateFormat()
        val e = uiGame.endDate!!.toCsvDateFormat()
        val g = "Test Game Name 1,$s,$e"
        val expected =
            "Round,Winner,Discarder,Hand Points,Points Test Player 1,Points Test Player 2,Points Test Player 3,Points Test Player 4,Penalty Test Player 1,Penalty Test Player 2,Penalty Test Player 3,Penalty Test Player 4,Game name,Game start date,Game end date\n" +
                "1,Test Player 1,Test Player 4,18,42,-8,-8,-26,0,0,0,0,$g\n" +
                "-,-,-,-,42,-8,-8,-26,-,-,-,-,-,-,-\n" +
                "2,Test Player 2,Test Player 3,9,-8,33,-17,-8,0,0,0,0,$g\n" +
                "-,-,-,-,34,25,-25,-34,-,-,-,-,-,-,-\n" +
                "3,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,$g\n" +
                "-,-,-,-,26,5,11,-42,-,-,-,-,-,-,-\n" +
                "4,Test Player 4,Test Player 1,25,-33,-8,-8,49,0,0,0,0,$g\n" +
                "-,-,-,-,-7,-3,3,7,-,-,-,-,-,-,-\n" +
                "5,Test Player 4,-,8,-16,-16,-16,48,0,0,0,0,$g\n" +
                "-,-,-,-,-23,-19,-13,55,-,-,-,-,-,-,-\n" +
                "6,Test Player 2,Test Player 4,12,-38,46,2,-10,-30,10,10,10,$g\n" +
                "-,-,-,-,-61,27,-11,45,-,-,-,-,-,-,-\n" +
                "7,Test Player 1,Test Player 3,10,34,-8,-18,-8,0,0,0,0,$g\n" +
                "-,-,-,-,-27,19,-29,37,-,-,-,-,-,-,-\n" +
                "8,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,$g\n" +
                "-,-,-,-,-35,-1,7,29,-,-,-,-,-,-,-\n" +
                "9,Test Player 4,Test Player 1,17,-25,-8,-8,41,0,0,0,0,$g\n" +
                "-,-,-,-,-60,-9,-1,70,-,-,-,-,-,-,-\n" +
                "10,Test Player 4,-,8,-16,-16,-16,48,0,0,0,0,$g\n" +
                "-,-,-,-,-76,-25,-17,118,-,-,-,-,-,-,-\n" +
                "11,Test Player 2,Test Player 4,28,-68,72,-18,-16,-60,20,-10,20,$g\n" +
                "-,-,-,-,-144,47,-35,102,-,-,-,-,-,-,-\n" +
                "12,Test Player 1,Test Player 3,9,33,-8,-17,-8,0,0,0,0,$g\n" +
                "-,-,-,-,-111,39,-52,94,-,-,-,-,-,-,-\n" +
                "13,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,$g\n" +
                "-,-,-,-,-119,19,-16,86,-,-,-,-,-,-,-\n" +
                "14,Test Player 3,Test Player 4,13,-8,-8,37,-21,0,0,0,0,$g\n" +
                "-,-,-,-,-127,11,21,65,-,-,-,-,-,-,-\n" +
                "15,Test Player 4,Test Player 3,8,-8,-8,-16,32,0,0,0,0,$g\n" +
                "-,-,-,-,-135,3,5,97,-,-,-,-,-,-,-\n" +
                "16,Test Player 4,Test Player 2,24,-8,-32,-8,48,0,0,0,0,$g\n" +
                "-,-,-,-,-143,-29,-3,145,-,-,-,-,-,-,-\n"
        assertEquals(expected, exportGameToCsvUseCase.buildCsvText(uiGame))
    }

    @Test
    fun exportAndImportAGameTest() = runTest {
        val uiGameToExport = createCompleteGame()
        importGamesFromJsonUseCase(jsonFrom(listOf(uiGameToExport))).getOrThrow()

        val games = gamesRepository.getAllFlow().first()
        assertEquals(2, games.size)
        val imported = games.first()
        assertEquals(2L, imported.gameId)
        assertEquals("Test Game Name 1", imported.gameName)
        assertEquals("Test Player 1", imported.nameP1)
        assertEquals("Test Player 4", imported.nameP4)
        assertEquals(uiGameToExport.startDate, imported.startDate)
        assertNotNull(imported.endDate)

        val importedUiGame = imported.toUiGame(roundsRepository.getGameRounds(imported.gameId).getOrThrow())
        assertEquals(16, importedUiGame.uiRounds.size)
        assertTrue(importedUiGame.uiRounds[10].isBestHand)
        val last = importedUiGame.uiRounds.last()
        assertEquals(18L + uiGameToExport.uiRounds.size, last.roundId)
        assertEquals(16, last.roundNumber)
        assertEquals(listOf(-143, -29, -3, 145), listOf(last.totalPointsP1, last.totalPointsP2, last.totalPointsP3, last.totalPointsP4))
    }

    @Test
    fun exportAndImportSeveralGamesTest() = runTest {
        val game1 = createCompleteGame("Test_Game_Name_1", "Test Player 11", "Test Player 12", "Test Player 13", "Test Player 14")
        val game2 = createCompleteGame("Test Game Name 2", "Test Player 21", "Test Player 22", "Test Player 23", "Test Player 24")
        var game3 = createCompleteGame("Test Game Name 3", "Test Player 31", "Test Player 32", "Test Player 33", "Test Player 34", ended = false)
        // Make 3rd game last round ongoing
        roundsRepository.deleteOne(game3.uiRounds.last().roundId)
        roundsRepository.insertOne(RoundRecord(game3.gameId))
        game3 = gamesRepository.getOne(game3.gameId).getOrThrow()
            .toUiGame(roundsRepository.getGameRounds(game3.gameId).getOrThrow())

        importGamesFromJsonUseCase(jsonFrom(listOf(game1, game2, game3))).getOrThrow()

        val games = gamesRepository.getAllFlow().first()
        assertEquals(6, games.size)

        fun checkImported(index: Int, gameId: Long, name: String, playersPrefix: String, lastRoundId: Long) {
            val imported = games[index]
            assertEquals(gameId, imported.gameId)
            assertEquals(name, imported.gameName)
            assertEquals(games[index + 1].gameName, imported.gameName)
            assertEquals("${playersPrefix}1", imported.nameP1)
            assertEquals("${playersPrefix}4", imported.nameP4)
            assertEquals(games[index + 1].startDate, imported.startDate)
            val rounds = roundsRepository.rounds.value.filter { it.gameId == gameId }.sortedBy { it.roundId }
            val ui = imported.toUiGame(rounds)
            assertEquals(16, ui.uiRounds.size)
            assertTrue(ui.uiRounds[10].isBestHand)
            assertEquals(lastRoundId, ui.uiRounds.last().roundId)
            assertEquals(16, ui.uiRounds.last().roundNumber)
        }
        checkImported(4, 4, "Test_Game_Name_1", "Test Player 1", 18L * 3 + 16 * 1 + 1)
        checkImported(2, 5, "Test Game Name 2", "Test Player 2", 18L * 3 + 16 * 2 + 1)
        checkImported(0, 6, "Test Game Name 3", "Test Player 3", 18L * 3 + 16 * 3 + 1)

        assertNotNull(games[4].endDate)
        assertNotNull(games[2].endDate)
        assertNull(games[0].endDate)
        val last = games[0].toUiGame(roundsRepository.getGameRounds(6).getOrThrow()).uiRounds.last()
        assertNull(last.winnerInitialSeat)
        assertNull(last.discarderInitialSeat)
        assertEquals(0, last.handPoints)
        assertEquals(listOf(0, 0, 0, 0), listOf(last.pointsP1, last.pointsP2, last.pointsP3, last.pointsP4))
        assertEquals(listOf(-135, 3, 5, 97), listOf(last.totalPointsP1, last.totalPointsP2, last.totalPointsP3, last.totalPointsP4))
    }

    @Test
    fun invalidJsonIsRejectedAndNothingIsImported() = runTest {
        createCompleteGame()
        val result = importGamesFromJsonUseCase("{ this is not valid json")
        assertTrue(result.isFailure)
        assertEquals(1, gamesRepository.getAllFlow().first().size)
    }

    @Test
    fun wrongFileNameTest() = runTest {
        val game = createCompleteGame(gameName = "Weird/Game/Name/1")
        assertEquals("Weird_Game_Name_1.csv", exportGameToCsvUseCase(game.gameId).getOrThrow().fileName)
        assertEquals("Weird_Game_Name_1.json", exportGameToJsonUseCase(game.gameId).getOrThrow().fileName)
    }

    private suspend fun createCompleteGame(
        gameName: String = "Test Game Name 1",
        nameP1: String = "Test Player 1",
        nameP2: String = "Test Player 2",
        nameP3: String = "Test Player 3",
        nameP4: String = "Test Player 4",
        ended: Boolean = true,
    ): UiGame {
        val startDate = nextStartDate
        nextStartDate += 1_000
        val gameRecord = GameRecord(
            gameId = NOT_SET_GAME_ID,
            gameName = gameName,
            nameP1 = nameP1, nameP2 = nameP2, nameP3 = nameP3, nameP4 = nameP4,
            startDate = startDate,
            endDate = if (ended) startDate + 1_000 else null,
        )
        val gameId = gamesRepository.insertOne(gameRecord).getOrThrow()

        suspend fun round(w: TableWinds?, d: TableWinds?, pts: Int, p1: Int = 0, p2: Int = 0, p3: Int = 0, p4: Int = 0) =
            roundsRepository.insertOne(RoundRecord(gameId, 0, w, d, pts, p1, p2, p3, p4)).getOrThrow()
        suspend fun insertAndDeleteEmptyRound() {
            round(null, null, 0)
            roundsRepository.deleteOne(roundsRepository.rounds.value.last().roundId)
        }

        round(EAST, NORTH, 18)
        round(SOUTH, WEST, 9)
        round(WEST, SOUTH, 12)
        round(NORTH, EAST, 25)
        round(NORTH, NONE, 8)
        round(SOUTH, NORTH, 12, -30, 10, 10, 10)
        round(EAST, WEST, 10)
        round(WEST, SOUTH, 12)
        insertAndDeleteEmptyRound()
        round(NORTH, EAST, 17)
        round(NORTH, NONE, 8)
        round(SOUTH, NORTH, 28, -60, 20, -10, 20)
        round(EAST, WEST, 9)
        round(WEST, SOUTH, 12)
        round(WEST, NORTH, 13)
        round(NORTH, WEST, 8)
        insertAndDeleteEmptyRound()
        round(NORTH, SOUTH, 24)

        return gameRecord.copy(gameId = gameId).toUiGame(roundsRepository.getGameRounds(gameId).getOrThrow())
    }
}
