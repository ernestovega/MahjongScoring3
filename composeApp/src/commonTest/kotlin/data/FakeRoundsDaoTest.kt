package data

import data.database.tables.DbRound
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST
import domain.model.exceptions.RoundNotFoundException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ui.common.components.GameId
import ui.common.components.RoundId
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeRoundsDaoTest {

    private lateinit var dao: FakeRoundsDao

    @BeforeTest
    fun setUp() {
        dao = FakeRoundsDao()
    }

    @Test
    fun testInsertAndGetOne() = runTest {
        val round = createTestRound(gameId = 1L, handPoints = 10)
        val insertedId = dao.insertOne(round)
        val retrievedRound = dao.getOne(insertedId)
        assertEquals(insertedId, retrievedRound.roundId)
        assertEquals(1L, retrievedRound.gameId)
        assertEquals(10, retrievedRound.handPoints)
    }

    @Test
    fun testGetAllFlow() = runTest {
        val round1 = createTestRound(gameId = 1L, handPoints = 10)
        val round2 = createTestRound(gameId = 2L, handPoints = 20)
        dao.insertOne(round1)
        dao.insertOne(round2)
        val allRounds = dao.getAllFlow().first()
        assertEquals(2, allRounds.size)
    }

    @Test
    fun testGetGameRoundsFlow() = runTest {
        val gameId1: GameId = 1L
        val gameId2: GameId = 2L
        val round1 = createTestRound(gameId = gameId1, handPoints = 10)
        val round2 = createTestRound(gameId = gameId1, handPoints = 20)
        val round3 = createTestRound(gameId = gameId2, handPoints = 30)
        dao.insertOne(round1)
        dao.insertOne(round2)
        dao.insertOne(round3)
        val game1Rounds = dao.getGameRoundsFlow(gameId1).first()
        assertEquals(2, game1Rounds.size)
        game1Rounds.forEach { assertEquals(gameId1, it.gameId) }
    }

    @Test
    fun testGetGameRounds() = runTest {
        val gameId: GameId = 1L
        val round1 = createTestRound(gameId = gameId, handPoints = 10)
        val round2 = createTestRound(gameId = gameId, handPoints = 20)
        val roundId1 = dao.insertOne(round1)
        val roundId2 = dao.insertOne(round2)
        val rounds = dao.getGameRounds(gameId)
        assertEquals(2, rounds.size)
        // Verify that rounds are sorted by roundId (ascending).
        assertEquals(roundId1, rounds[0].roundId)
        assertEquals(roundId2, rounds[1].roundId)
    }

    @Test
    fun testUpdateOne() = runTest {
        val round = createTestRound(gameId = 1L, handPoints = 10)
        val insertedId = dao.insertOne(round)
        // Create an updated round using the inserted roundId.
        val updatedRound = round.copy(roundId = insertedId).apply {
            handPoints = 50
            winnerInitialSeat = SOUTH
            discarderInitialSeat = WEST
            penaltyP1 = 100
            penaltyP2 = 200
            penaltyP3 = 300
            penaltyP4 = 400
        }
        dao.updateOne(updatedRound)
        val retrievedRound = dao.getOne(insertedId)
        assertEquals(50, retrievedRound.handPoints)
        assertEquals(SOUTH, retrievedRound.winnerInitialSeat)
        assertEquals(WEST, retrievedRound.discarderInitialSeat)
        assertEquals(100, retrievedRound.penaltyP1)
        assertEquals(200, retrievedRound.penaltyP2)
        assertEquals(300, retrievedRound.penaltyP3)
        assertEquals(400, retrievedRound.penaltyP4)
    }

    @Test
    fun testDeleteGameRounds() = runTest {
        val gameIdToDelete: GameId = 1L
        val gameIdOther: GameId = 2L
        val round1 = createTestRound(gameId = gameIdToDelete, handPoints = 10)
        val round2 = createTestRound(gameId = gameIdToDelete, handPoints = 20)
        val round3 = createTestRound(gameId = gameIdOther, handPoints = 30)
        dao.insertOne(round1)
        dao.insertOne(round2)
        dao.insertOne(round3)
        val deletedCount = dao.deleteGameRounds(gameIdToDelete)
        assertEquals(2, deletedCount)
        // Verify rounds for gameIdToDelete are deleted.
        val roundsForDeletedGame = dao.getGameRounds(gameIdToDelete)
        assertEquals(0, roundsForDeletedGame.size)
        // Verify rounds for the other game remain.
        val roundsForOtherGame = dao.getGameRounds(gameIdOther)
        assertEquals(1, roundsForOtherGame.size)
    }

    @Test
    fun testDeleteOne() = runTest {
        val round = createTestRound(gameId = 1L, handPoints = 10)
        val insertedId = dao.insertOne(round)
        dao.deleteOne(insertedId)
        assertFailsWith<RoundNotFoundException> {
            dao.getOne(insertedId)
        }
    }

    @Test
    fun testGetOneNotFound() = runTest {
        assertFailsWith<RoundNotFoundException> {
            dao.getOne(999L)
        }
    }

    // The provided dummyRoundId is ignored by FakeRoundsDao as it assigns a new id.
    private fun createTestRound(
        gameId: GameId,
        dummyRoundId: RoundId = 0,
        handPoints: Int = 0
    ): DbRound {
        return DbRound(gameId = gameId, roundId = dummyRoundId).apply {
            this.handPoints = handPoints
            this.winnerInitialSeat = EAST
            this.discarderInitialSeat = NORTH
            this.penaltyP1 = 5
            this.penaltyP2 = 10
            this.penaltyP3 = 15
            this.penaltyP4 = 20
        }
    }
}