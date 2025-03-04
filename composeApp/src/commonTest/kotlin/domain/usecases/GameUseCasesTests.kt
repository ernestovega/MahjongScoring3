package domain.usecases

import data.FakeAppDatabase
import data.FakeGamesDao
import data.FakeRoundsDao
import data.database.room.tables.DbGame
import data.repositories.games.DefaultGamesRepository
import data.repositories.rounds.DefaultRoundsRepository
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import ui.common.components.MAX_MCR_ROUNDS
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameUseCasesTests {

    private lateinit var database: FakeAppDatabase

    private lateinit var gamesDao: FakeGamesDao
    private lateinit var roundsDao: FakeRoundsDao

    private lateinit var gamesRepository: DefaultGamesRepository
    private lateinit var roundsRepository: DefaultRoundsRepository

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
    private val testScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @BeforeTest
    fun setUp() {
        database = FakeAppDatabase()

        gamesDao = database.gamesDao
        roundsDao = database.roundsDao

        gamesRepository = DefaultGamesRepository(gamesDao)
        roundsRepository = DefaultRoundsRepository(roundsDao)

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
        val games = gamesDao.getAll()
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

        val rounds = roundsDao.getGameRounds(1)
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
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for ending the game
        endGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a not null game endDate, and an ongoing round
        game = gamesDao.getAll().first()
        assertNotNull(game.endDate)

        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun resumeGameUseCase() = runTest {
        // Given an ended game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        endGameUseCase.invoke(game.toUiGame(rounds))

        // When we call the UseCase for resuming the game
        game = gamesDao.getAll().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        resumeGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a null game endDate, and an ongoing round
        game = gamesDao.getAll().first()
        assertNull(game.endDate)

        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertNull(firstRound.winnerInitialSeat)
        assertNull(firstRound.discarderInitialSeat)
    }

    @Test
    fun huDrawUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Draw
        huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)

        // Then we expect 2 rounds, the 1st with Draw data and the 2nd ongoing
        rounds = roundsDao.getGameRounds(game.gameId)
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
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Hu by self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, 8)

        // Then we expect 2 rounds, the 1st with the Hu by self pick data and the 2nd ongoing
        game = gamesDao.getAll().first()
        rounds = roundsDao.getGameRounds(game.gameId)

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
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Hu by discard
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, SOUTH, 9)

        // Then we expect 2 rounds, the 1st with Hu by discard data and the 2nd ongoing
        game = gamesDao.getAll().first()
        rounds = roundsDao.getGameRounds(game.gameId)

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
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS - 1) {
            huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When set the 16th hand by Hu self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, 8)

        // Then we expect MAX_MCR_ROUNDS rounds, none ongoing and a not null game endDate
        rounds = roundsDao.getGameRounds(game.gameId)
        assertEquals(rounds.size, MAX_MCR_ROUNDS)

        val lastRound = rounds.last()
        assertNotNull(lastRound.winnerInitialSeat)
        assertNotNull(lastRound.discarderInitialSeat)

        game = gamesDao.getAll().first()
        assertNotNull(game.endDate)
    }

    @Test
    fun deleteRoundUseCase() = runTest {
        // Given an existing game with 2 rounds, last 1 ongoing
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)

        // When we call the UseCase to delete the round 1
        var firstRound = rounds.first()
        deleteRoundUseCase.invoke(firstRound.roundId)

        // Then we expect 1 round ongoing
        rounds = roundsDao.getGameRounds(game.gameId)
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
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS) {
            huDrawUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound)
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When we call the UseCase to delete the last round
        var lastRound = rounds.last()
        deleteRoundUseCase.invoke(lastRound.roundId)

        // Then we expect 15 rounds, none ongoing and game endDate not null
        rounds = roundsDao.getGameRounds(game.gameId)
        assertEquals(rounds.size, 15)

        lastRound = rounds.last()
        assertEquals(lastRound.roundId, 15)
        assertNotNull(lastRound.winnerInitialSeat)
        assertNotNull(lastRound.discarderInitialSeat)

        game = gamesDao.getAll().first()
        assertNotNull(game.endDate)
    }

    @Test
    fun setDividedPenaltyUseCase() = runTest {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), true, EAST, 30)

        // Then we expect 1 ongoing round with the right penalty data
        game = gamesDao.getAll().first()
        rounds = roundsDao.getGameRounds(game.gameId)
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
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), false, EAST, 30)

        // Then we expect 1 ongoing round with the right penalty data
        rounds = roundsDao.getGameRounds(game.gameId)
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
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        cancelAllPenaltiesUseCase.invoke(game.toUiGame(rounds).uiRounds.first())

        // Then we expect 1 ongoing round with no penalties
        rounds = roundsDao.getGameRounds(game.gameId)
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
        val rounds = roundsDao.getGameRounds(game.gameId)

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
        game = gamesDao.getAll().first()
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
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, NORTH, 18) // 1
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, WEST, 9) // 2
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 3
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, EAST, 25) // 4
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 5
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, true, EAST, 30) // 6
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, NORTH, 12) // 6
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, WEST, 10) // 7
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 8
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 9
        deleteRoundUseCase.invoke(9) // 9
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, EAST, 17) // 9 (10)
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 8) // 10 (11)
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, true, EAST, 60) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, false, WEST, 30) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, SOUTH, NORTH, 28) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, EAST, WEST, 9) // 12 (13)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, SOUTH, 12) // 13 (14)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, WEST, NORTH, 13) // 14 (15)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, WEST, 8) // 15 (16)
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, 1008) // 16 (17)
        deleteRoundUseCase.invoke(17) // 16 (17)
        rounds = roundsDao.getGameRounds(game.gameId)
        resumeGameUseCase.invoke(game.toUiGame(rounds))
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds).ongoingOrLastRound, NORTH, SOUTH, 24) // 16 (18)

        game = gamesDao.getOne(game.gameId)
        rounds = roundsDao.getGameRounds(game.gameId)
        return game.toUiGame(rounds)
    }

    private suspend fun createJustGameAndFirstOngoingRound(gameName: String = "Test Game Name 1"): DbGame =
        createGameUseCase.invoke(
            gameName = gameName,
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        ).getOrThrow().let { gameId -> gamesDao.getOne(gameId) }
}