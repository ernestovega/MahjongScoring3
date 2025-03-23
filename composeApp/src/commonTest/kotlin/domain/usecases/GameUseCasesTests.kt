package domain.usecases

import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.MS3Database
import createTestDatabase
import data.repositories.games.DefaultGamesDataSource
import data.repositories.games.DefaultGamesRepository
import data.repositories.games.GamesDataSource
import data.repositories.games.GamesRepository
import data.repositories.rounds.DefaultRoundsDataSource
import data.repositories.rounds.DefaultRoundsRepository
import data.repositories.rounds.RoundsDataSource
import data.repositories.rounds.RoundsRepository
import domain.model.UiGame
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NONE
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST
import domain.use_cases.CancelAllPenaltiesUseCase
import domain.use_cases.CreateGameUseCase
import domain.use_cases.DeleteRoundUseCase
import domain.use_cases.EditGameNamesUseCase
import domain.use_cases.EndGameUseCase
import domain.use_cases.EndRoundUseCase
import domain.use_cases.GetOneGameUseCase
import domain.use_cases.HuDiscardUseCase
import domain.use_cases.HuDrawUseCase
import domain.use_cases.HuSelfPickUseCase
import domain.use_cases.ResumeGameUseCase
import domain.use_cases.SetPenaltyUseCase
import domain.use_cases.mappers.toUiGame
import domain.use_cases.utils.second
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.etologic.mahjongscoring.common.components.MAX_MCR_ROUNDS
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameUseCasesTests {

    private lateinit var database: MS3Database
    private lateinit var gamesDataSource: GamesDataSource
    private lateinit var roundsDataSource: RoundsDataSource
    private lateinit var gamesRepository: GamesRepository
    private lateinit var roundsRepository: RoundsRepository
    private lateinit var createGameUseCase: CreateGameUseCase
    private lateinit var endGameUseCase: EndGameUseCase
    private lateinit var resumeGameUseCase: ResumeGameUseCase
    private lateinit var endRoundUseCase: EndRoundUseCase
    private lateinit var huDrawUseCase: HuDrawUseCase
    private lateinit var huSelfPickUseCase: HuSelfPickUseCase
    private lateinit var huDiscardUseCase: HuDiscardUseCase
    private lateinit var deleteRoundUseCase: DeleteRoundUseCase
    private lateinit var setPenaltyUseCase: SetPenaltyUseCase
    private lateinit var cancelAllPenaltiesUseCase: CancelAllPenaltiesUseCase
    private lateinit var editGameNamesUseCase: EditGameNamesUseCase

    @BeforeTest
    fun setUp() {
        database = createTestDatabase()
        gamesDataSource = DefaultGamesDataSource(database)
        roundsDataSource = DefaultRoundsDataSource(database)
        gamesRepository = DefaultGamesRepository(gamesDataSource)
        roundsRepository = DefaultRoundsRepository(roundsDataSource)
        val getOneGameUseCase = GetOneGameUseCase(gamesRepository, roundsRepository)
        createGameUseCase = CreateGameUseCase(gamesRepository, roundsRepository)
        endGameUseCase = EndGameUseCase(gamesRepository)
        resumeGameUseCase = ResumeGameUseCase(gamesRepository, roundsRepository)
        endRoundUseCase = EndRoundUseCase(roundsRepository, endGameUseCase, getOneGameUseCase)
        huDrawUseCase = HuDrawUseCase(roundsRepository, endRoundUseCase)
        huSelfPickUseCase = HuSelfPickUseCase(roundsRepository, endRoundUseCase)
        huDiscardUseCase = HuDiscardUseCase(roundsRepository, endRoundUseCase)
        deleteRoundUseCase = DeleteRoundUseCase(roundsRepository)
        setPenaltyUseCase = SetPenaltyUseCase(roundsRepository)
        cancelAllPenaltiesUseCase = CancelAllPenaltiesUseCase(roundsRepository)
        editGameNamesUseCase = EditGameNamesUseCase(gamesRepository)
    }

    @Test
    fun createGameUseCase() = runTest {
        // Given an empty db
        // When we call the UseCase for creating a game
        createJustGameAndFirstOngoingRound()

        // Then we expect the right game values and the first ongoing round
        val games = gamesRepository.getAllFlow().first()
        assertEquals(games.size, 1)

        val game = games.first()
        assertEquals(game.gameId, 1)
        assertEquals(game.gameName, "Test Game Name 1")
        assertEquals(game.nameP1, "Test Player 1")
        assertEquals(game.nameP2, "Test Player 2")
        assertEquals(game.nameP3, "Test Player 3")
        assertEquals(game.nameP4, "Test Player 4")
        assertNotNull(game.startDate)
        assertNull(game.endDate)

        val rounds = roundsRepository.getGameRounds(1).getOrThrow()
        assertEquals(rounds.size, 1)

        val firstRound = rounds.first()
        assertEquals(firstRound.gameId, 1)
        assertEquals(firstRound.roundId, 1)
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
        assertEquals(firstRound.handPoints, 0)
        assertEquals(firstRound.penaltyP1, 0)
        assertEquals(firstRound.penaltyP2, 0)
        assertEquals(firstRound.penaltyP3, 0)
        assertEquals(firstRound.penaltyP4, 0)
    }

    @Test
    fun endGameUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for ending the game
        endGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a not null game endDate, and an ongoing round
        game = gamesRepository.getAllFlow().first().first()
        assertNotNull(game.endDate)

        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun resumeGameUseCase() = runTest {
        // Given an ended game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        endGameUseCase.invoke(game.toUiGame(rounds))

        // When we call the UseCase for resuming the game
        game = gamesRepository.getAllFlow().first().first()
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        resumeGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a null game endDate, and an ongoing round
        game = gamesRepository.getAllFlow().first().first()
        assertNull(game.endDate)

        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun huDrawUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a Draw
        huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)

        // Then we expect 2 rounds, the 1st with Draw data and the 2nd ongoing
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        assertEquals(rounds.size, 2)
        val firstRound = rounds.first()
        assertEquals(firstRound.winnerInitialSeat, NONE)
        assertEquals(firstRound.discarderInitialSeat, NONE)
        assertEquals(firstRound.handPoints, 0)
        val secondRound = rounds.second()
        assertNull(secondRound.winnerInitialSeat)
        assertNull(secondRound.discarderInitialSeat)
    }

    @Test
    fun huSelfPickUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a Hu by self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, 8)

        // Then we expect 2 rounds, the 1st with the Hu by self pick data and the 2nd ongoing
        game = gamesRepository.getAllFlow().first().first()
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        val firstRound = rounds.first()
        assertEquals(firstRound.winnerInitialSeat, EAST)
        assertEquals(firstRound.discarderInitialSeat, NONE)
        assertEquals(firstRound.handPoints, 8)

        val secondRound = rounds.last()
        assertNull(secondRound.winnerInitialSeat)
        assertNull(secondRound.discarderInitialSeat)
    }

    @Test
    fun huDiscardUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a Hu by discard
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, SOUTH, 9)

        // Then we expect 2 rounds, the 1st with Hu by discard data and the 2nd ongoing
        game = gamesRepository.getAllFlow().first().first()
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        val firstRound = rounds.first()
        assertEquals(firstRound.winnerInitialSeat, EAST)
        assertEquals(firstRound.discarderInitialSeat, SOUTH)
        assertEquals(firstRound.handPoints, 9)

        val secondRound = rounds.last()
        assertNull(secondRound.winnerInitialSeat)
        assertNull(secondRound.discarderInitialSeat)
    }

    @Test
    fun end16thRoundUseCase() = runTest {
        // Given an existing game with the 16th round ongoing
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        repeat(MAX_MCR_ROUNDS - 1) {
            huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)
            rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        }

        // When set the 16th hand by Hu self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, 8)

        // Then we expect MAX_MCR_ROUNDS rounds, none ongoing and a not null game endDate
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        assertEquals(rounds.size, MAX_MCR_ROUNDS)

        val lastRound = rounds.last()
        assertNotNull(lastRound.winnerInitialSeat)
        assertNotNull(lastRound.discarderInitialSeat)

        game = gamesRepository.getAllFlow().first().first()
        assertNotNull(game.endDate)
    }

    @Test
    fun deleteRoundUseCase() = runTest {
        // Given an existing game with 2 rounds, last 1 ongoing
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)

        // When we call the UseCase to delete the round 1
        var firstRound = rounds.first()
        deleteRoundUseCase.invoke(firstRound.roundId)

        // Then we expect 1 round ongoing
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        assertEquals(rounds.size, 1)

        firstRound = rounds.first()
        assertEquals(firstRound.roundId, 2)
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun delete16thRoundUseCase() = runTest {
        // Given an ended game with MAX_MCR_ROUNDS rounds
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        repeat(MAX_MCR_ROUNDS) {
            huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)
            rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        }

        // When we call the UseCase to delete the last round
        var lastRound = rounds.last()
        deleteRoundUseCase.invoke(lastRound.roundId)

        // Then we expect 15 rounds, none ongoing and game endDate not null
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        assertEquals(rounds.size, 15)

        lastRound = rounds.last()
        assertEquals(lastRound.roundId, 15)
        assertNotNull(lastRound.winnerInitialSeat)
        assertNotNull(lastRound.discarderInitialSeat)

        game = gamesRepository.getAllFlow().first().first()
        assertNotNull(game.endDate)
    }

    @Test
    fun setDividedPenaltyUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), true, EAST, 30)

        // Then we expect 1 ongoing round with the right penalty data
        game = gamesRepository.getAllFlow().first().first()
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
        assertEquals(firstRound.penaltyP1, -30)
        assertEquals(firstRound.penaltyP2, +10)
        assertEquals(firstRound.penaltyP3, +10)
        assertEquals(firstRound.penaltyP4, +10)
    }

    @Test
    fun setNotDividedPenaltyUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), false, EAST, 30)

        // Then we expect 1 ongoing round with the right penalty data
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
        assertEquals(firstRound.penaltyP1, -30)
        assertEquals(firstRound.penaltyP2, 0)
        assertEquals(firstRound.penaltyP3, 0)
        assertEquals(firstRound.penaltyP4, 0)
    }

    @Test
    fun cancelPenaltiesUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a penalty divided
        cancelAllPenaltiesUseCase.invoke(game.toUiGame(rounds).uiRounds.first())

        // Then we expect 1 ongoing round with no penalties
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
        assertEquals(firstRound.penaltyP1, 0)
        assertEquals(firstRound.penaltyP2, 0)
        assertEquals(firstRound.penaltyP3, 0)
        assertEquals(firstRound.penaltyP4, 0)
    }

    @Test
    fun editGameNamesUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        val rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()

        // When we call the UseCase for setting a penalty divided
        editGameNamesUseCase.invoke(
            uiGame = game.toUiGame(rounds),
            newGameName = "EDITED game name 1",
            newNameP1 = "EDITED Player 1",
            newNameP2 = "EDITED Player 2",
            newNameP3 = "EDITED Player 3",
            newNameP4 = "EDITED Player 4",
        )

        // Then we expect 1 ongoing round with no penalties
        game = gamesRepository.getAllFlow().first().first()
        assertEquals(game.gameName, "EDITED game name 1")
        assertEquals(game.nameP1, "EDITED Player 1")
        assertEquals(game.nameP2, "EDITED Player 2")
        assertEquals(game.nameP3, "EDITED Player 3")
        assertEquals(game.nameP4, "EDITED Player 4")
    }

    @Test
    fun fullGameTest() = runTest {
        // Given a complete game
        val uiGame = createCompleteGame()

        // Then we check the expected data
        assertNotNull(uiGame.endDate)
        assertEquals(uiGame.uiRounds.size, 16)
        val lastUiRound = uiGame.uiRounds.last()
        assertEquals(lastUiRound.roundId, 18)
        assertEquals(lastUiRound.roundNumber, 16)
        assertEquals(lastUiRound.totalPointsP1, -143)
        assertEquals(lastUiRound.totalPointsP2, -29)
        assertEquals(lastUiRound.totalPointsP3, -3)
        assertEquals(lastUiRound.totalPointsP4, 145)
        assertTrue(uiGame.uiRounds[10].isBestHand)
    }

    private suspend fun createCompleteGame(): UiGame {
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, NORTH, 18) // 1
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, WEST, 9) // 2
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 3
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, EAST, 25) // 4
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 5
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, true, EAST, 30) // 6
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, NORTH, 12) // 6
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, WEST, 10) // 7
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 8
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 9
        deleteRoundUseCase.invoke(9) // 9
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, EAST, 17) // 9 (10)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 10 (11)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, true, EAST, 60) // 11 (12)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, false, WEST, 30) // 11 (12)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, NORTH, 28) // 11 (12)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, WEST, 9) // 12 (13)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 13 (14)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, NORTH, 13) // 14 (15)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, WEST, 8) // 15 (16)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 1008) // 16 (17)
        deleteRoundUseCase.invoke(17) // 16 (17)
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        resumeGameUseCase.invoke(game.toUiGame(rounds))
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, SOUTH, 24) // 16 (18)

        game = gamesRepository.getOne(game.gameId).getOrThrow()
        rounds = roundsRepository.getGameRounds(game.gameId).getOrThrow()
        return game.toUiGame(rounds)
    }

    private suspend fun createJustGameAndFirstOngoingRound(gameName: String = "Test Game Name 1"): DbGame =
        createGameUseCase.invoke(
            gameName = gameName,
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
            .getOrThrow()
            .let { gameId -> gamesRepository.getOne(gameId) }
            .getOrThrow()
}