package com.etologic.mahjongscoring3.domain

import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.HuData
import com.etologic.mahjongscoring3.domain.model.PenaltyData
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring3.domain.model.toUiGame
import com.etologic.mahjongscoring3.domain.usecases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring3.domain.usecases.CreateGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.DeleteRoundUseCase
import com.etologic.mahjongscoring3.domain.usecases.EditGameNamesUseCase
import com.etologic.mahjongscoring3.domain.usecases.EndGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.EndRoundUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDiscardUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDrawUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuSelfPickUseCase
import com.etologic.mahjongscoring3.domain.usecases.ResumeGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.SetPenaltyUseCase
import com.etologic.mahjongscoring3.domain.utils.TimeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Port of MS2 GameUseCasesTests, using in-memory fake repositories instead of Room. */
class GameUseCasesTest {

    private val gamesRepository = FakeGamesRepository()
    private val roundsRepository = FakeRoundsRepository()
    private val timeProvider = TimeProvider { 1_700_000_000_000L }

    private val getOneGameFlowUseCase = GetOneGameFlowUseCase(gamesRepository, roundsRepository)
    private val createGameUseCase = CreateGameUseCase(gamesRepository, roundsRepository, timeProvider)
    private val endGameUseCase = EndGameUseCase(gamesRepository, timeProvider)
    private val resumeGameUseCase = ResumeGameUseCase(gamesRepository, roundsRepository)
    private val endRoundUseCase = EndRoundUseCase(roundsRepository, endGameUseCase, getOneGameFlowUseCase)
    private val huDrawUseCase = HuDrawUseCase(roundsRepository, endRoundUseCase)
    private val huSelfPickUseCase = HuSelfPickUseCase(roundsRepository, endRoundUseCase)
    private val huDiscardUseCase = HuDiscardUseCase(roundsRepository, endRoundUseCase)
    private val deleteRoundUseCase = DeleteRoundUseCase(roundsRepository)
    private val setPenaltyUseCase = SetPenaltyUseCase(roundsRepository)
    private val cancelAllPenaltiesUseCase = CancelAllPenaltiesUseCase(roundsRepository)
    private val editGameNamesUseCase = EditGameNamesUseCase(gamesRepository)

    private suspend fun rounds(gameId: Long) = roundsRepository.getGameRounds(gameId).getOrThrow()
    private suspend fun firstGame() = gamesRepository.getAllFlow().first().first()
    private suspend fun uiGame(game: GameRecord): UiGame = game.toUiGame(rounds(game.gameId))

    @Test
    fun createGameUseCase() = runTest {
        createJustGameAndFirstOngoingRound()

        val games = gamesRepository.getAllFlow().first()
        assertEquals(1, games.size)
        val game = games.first()
        assertEquals(1L, game.gameId)
        assertEquals("Test Game Name 1", game.gameName)
        assertEquals("Test Player 1", game.nameP1)
        assertEquals("Test Player 2", game.nameP2)
        assertEquals("Test Player 3", game.nameP3)
        assertEquals("Test Player 4", game.nameP4)
        assertNull(game.endDate)

        val rounds = rounds(1)
        assertEquals(1, rounds.size)
        val firstRound = rounds.first()
        assertEquals(1L, firstRound.gameId)
        assertEquals(1L, firstRound.roundId)
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
        assertEquals(0, firstRound.handPoints)
        assertEquals(listOf(0, 0, 0, 0), with(firstRound) { listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4) })
    }

    @Test
    fun endGameUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        endGameUseCase(uiGame(game)).getOrThrow()

        assertNotNull(firstGame().endDate)
        val firstRound = rounds(game.gameId).first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun resumeGameUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        endGameUseCase(uiGame(game)).getOrThrow()
        resumeGameUseCase(uiGame(firstGame())).getOrThrow()

        assertNull(firstGame().endDate)
        val rounds = rounds(game.gameId)
        assertEquals(1, rounds.size)
        assertNull(rounds.first().winnerInitialSeat)
    }

    @Test
    fun huDrawUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        huDrawUseCase(uiGame(game)).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(NONE, rounds.first().winnerInitialSeat)
        assertEquals(NONE, rounds.first().discarderInitialSeat)
        assertEquals(0, rounds.first().handPoints)
        assertNull(rounds.last().winnerInitialSeat)
        assertNull(rounds.last().discarderInitialSeat)
    }

    @Test
    fun huSelfPickUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        huSelfPickUseCase(uiGame(game), HuData(8, EAST)).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(EAST, rounds.first().winnerInitialSeat)
        assertEquals(NONE, rounds.first().discarderInitialSeat)
        assertEquals(8, rounds.first().handPoints)
        assertNull(rounds.last().winnerInitialSeat)
        assertNull(rounds.last().discarderInitialSeat)
    }

    @Test
    fun huDiscardUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        huDiscardUseCase(uiGame(game), HuData(9, EAST, SOUTH)).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(EAST, rounds.first().winnerInitialSeat)
        assertEquals(SOUTH, rounds.first().discarderInitialSeat)
        assertEquals(9, rounds.first().handPoints)
        assertNull(rounds.last().winnerInitialSeat)
        assertNull(rounds.last().discarderInitialSeat)
    }

    @Test
    fun end16thRoundUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        repeat(MAX_MCR_ROUNDS - 1) { huDrawUseCase(uiGame(game)).getOrThrow() }
        huSelfPickUseCase(uiGame(game), HuData(8, EAST)).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(MAX_MCR_ROUNDS, rounds.size)
        assertNotNull(rounds.last().winnerInitialSeat)
        assertNotNull(rounds.last().discarderInitialSeat)
        assertNotNull(firstGame().endDate)
    }

    @Test
    fun deleteRoundUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        val firstRoundId = rounds(game.gameId).first().roundId
        huDrawUseCase(uiGame(game)).getOrThrow()
        deleteRoundUseCase(firstRoundId).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(1, rounds.size)
        assertEquals(2L, rounds.first().roundId)
        assertNull(rounds.first().winnerInitialSeat)
        assertNull(rounds.first().discarderInitialSeat)
    }

    @Test
    fun deleteOngoingRoundCreatesANewOngoingOne() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        deleteRoundUseCase(rounds(game.gameId).first().roundId).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(1, rounds.size)
        assertEquals(2L, rounds.first().roundId)
        assertTrue(rounds.first().isOngoing())
    }

    @Test
    fun delete16thRoundUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        repeat(MAX_MCR_ROUNDS) { huDrawUseCase(uiGame(game)).getOrThrow() }
        deleteRoundUseCase(rounds(game.gameId).last().roundId).getOrThrow()

        val rounds = rounds(game.gameId)
        assertEquals(15, rounds.size)
        assertEquals(15L, rounds.last().roundId)
        assertNotNull(rounds.last().winnerInitialSeat)
        assertNotNull(rounds.last().discarderInitialSeat)
        assertNotNull(firstGame().endDate)
    }

    @Test
    fun setDividedPenaltyUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        setPenaltyUseCase(uiGame(game).uiRounds.first(), PenaltyData(30, true, EAST)).getOrThrow()

        val r = rounds(game.gameId).first()
        assertNull(r.winnerInitialSeat)
        assertNull(r.discarderInitialSeat)
        assertEquals(listOf(-30, 10, 10, 10), listOf(r.penaltyP1, r.penaltyP2, r.penaltyP3, r.penaltyP4))
    }

    @Test
    fun setNotDividedPenaltyUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        setPenaltyUseCase(uiGame(game).uiRounds.first(), PenaltyData(30, false, EAST)).getOrThrow()

        val r = rounds(game.gameId).first()
        assertNull(r.winnerInitialSeat)
        assertNull(r.discarderInitialSeat)
        assertEquals(listOf(-30, 0, 0, 0), listOf(r.penaltyP1, r.penaltyP2, r.penaltyP3, r.penaltyP4))
    }

    @Test
    fun cancelPenaltiesUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        setPenaltyUseCase(uiGame(game).uiRounds.first(), PenaltyData(30, true, EAST)).getOrThrow()
        cancelAllPenaltiesUseCase(uiGame(game).uiRounds.first()).getOrThrow()

        val r = rounds(game.gameId).first()
        assertNull(r.winnerInitialSeat)
        assertNull(r.discarderInitialSeat)
        assertEquals(listOf(0, 0, 0, 0), listOf(r.penaltyP1, r.penaltyP2, r.penaltyP3, r.penaltyP4))
    }

    @Test
    fun editGameNamesUseCase() = runTest {
        val game = createJustGameAndFirstOngoingRound()
        editGameNamesUseCase(
            uiGame = uiGame(game),
            newGameName = "EDITED game name 1",
            newNameP1 = "EDITED Player 1",
            newNameP2 = "EDITED Player 2",
            newNameP3 = "EDITED Player 3",
            newNameP4 = "EDITED Player 4",
        ).getOrThrow()

        val edited = firstGame()
        assertEquals("EDITED game name 1", edited.gameName)
        assertEquals("EDITED Player 1", edited.nameP1)
        assertEquals("EDITED Player 2", edited.nameP2)
        assertEquals("EDITED Player 3", edited.nameP3)
        assertEquals("EDITED Player 4", edited.nameP4)
    }

    @Test
    fun fullGameTest() = runTest {
        val uiGame = createCompleteGame(this@GameUseCasesTest)

        assertNotNull(uiGame.endDate)
        assertEquals(16, uiGame.uiRounds.size)
        val lastUiRound = uiGame.uiRounds.last()
        assertEquals(18L, lastUiRound.roundId)
        assertEquals(16, lastUiRound.roundNumber)
        assertEquals(-143, lastUiRound.totalPointsP1)
        assertEquals(-29, lastUiRound.totalPointsP2)
        assertEquals(-3, lastUiRound.totalPointsP3)
        assertEquals(145, lastUiRound.totalPointsP4)
        assertTrue(uiGame.uiRounds[10].isBestHand)
    }

    internal suspend fun createJustGameAndFirstOngoingRound(gameName: String = "Test Game Name 1"): GameRecord {
        val gameId = createGameUseCase(
            gameName = gameName,
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        ).getOrThrow()
        return gamesRepository.getOne(gameId).getOrThrow()
    }

    companion object {
        /** Plays the same complete game as MS2's tests and returns it. */
        internal suspend fun createCompleteGame(t: GameUseCasesTest): UiGame = with(t) {
            val g = createJustGameAndFirstOngoingRound()
            suspend fun ui() = uiGame(gamesRepository.getOne(g.gameId).getOrThrow())
            huDiscardUseCase(ui(), HuData(18, EAST, NORTH)).getOrThrow() // 1
            huDiscardUseCase(ui(), HuData(9, SOUTH, WEST)).getOrThrow() // 2
            huDiscardUseCase(ui(), HuData(12, WEST, SOUTH)).getOrThrow() // 3
            huDiscardUseCase(ui(), HuData(25, NORTH, EAST)).getOrThrow() // 4
            huSelfPickUseCase(ui(), HuData(8, NORTH)).getOrThrow() // 5
            setPenaltyUseCase(ui().uiRounds.last(), PenaltyData(30, true, EAST)).getOrThrow() // 6
            huDiscardUseCase(ui(), HuData(12, SOUTH, NORTH)).getOrThrow() // 6
            huDiscardUseCase(ui(), HuData(10, EAST, WEST)).getOrThrow() // 7
            huDiscardUseCase(ui(), HuData(12, WEST, SOUTH)).getOrThrow() // 8
            huSelfPickUseCase(ui(), HuData(8, NORTH)).getOrThrow() // 9
            deleteRoundUseCase(9).getOrThrow() // 9
            huDiscardUseCase(ui(), HuData(17, NORTH, EAST)).getOrThrow() // 9 (10)
            huSelfPickUseCase(ui(), HuData(8, NORTH)).getOrThrow() // 10 (11)
            setPenaltyUseCase(ui().uiRounds.last(), PenaltyData(60, true, EAST)).getOrThrow() // 11 (12)
            setPenaltyUseCase(ui().uiRounds.last(), PenaltyData(30, false, WEST)).getOrThrow() // 11 (12)
            huDiscardUseCase(ui(), HuData(28, SOUTH, NORTH)).getOrThrow() // 11 (12)
            huDiscardUseCase(ui(), HuData(9, EAST, WEST)).getOrThrow() // 12 (13)
            huDiscardUseCase(ui(), HuData(12, WEST, SOUTH)).getOrThrow() // 13 (14)
            huDiscardUseCase(ui(), HuData(13, WEST, NORTH)).getOrThrow() // 14 (15)
            huDiscardUseCase(ui(), HuData(8, NORTH, WEST)).getOrThrow() // 15 (16)
            huSelfPickUseCase(ui(), HuData(1008, NORTH)).getOrThrow() // 16 (17)
            deleteRoundUseCase(17).getOrThrow() // 16 (17)
            resumeGameUseCase(ui()).getOrThrow()
            huDiscardUseCase(ui(), HuData(24, NORTH, SOUTH)).getOrThrow() // 16 (18)
            ui()
        }
    }
}
