package data

import data.database.tables.DbGame
import domain.model.exceptions.GameNotFoundException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import ui.common.components.NOT_SET_GAME_ID
import kotlin.test.BeforeTest

class FakeGamesDaoTest {
    
    private lateinit var dao: FakeGamesDao
    
    @BeforeTest
    fun setup() {
        dao = FakeGamesDao()
    }

    @Test
    fun testInsertAndGetOne() = runTest {
        val game = createTestGame("2020-01-01T00:00:00Z")
        val insertedId = dao.insertOne(game)
        val retrievedGame = dao.getOne(insertedId)
        assertEquals(insertedId, retrievedGame.gameId)
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), retrievedGame.startDate)
    }

    @Test
    fun testGetAll() = runTest {
        val game1 = createTestGame("2020-01-01T00:00:00Z")
        val game2 = createTestGame("2021-01-01T00:00:00Z")
        val id1 = dao.insertOne(game1)
        val id2 = dao.insertOne(game2)

        // getAll should return games sorted by descending startDate.
        val allGames = dao.getAll()
        assertEquals(2, allGames.size)
        // Expect game2 (with later startDate) to appear first.
        assertEquals(id2, allGames[0].gameId)
        assertEquals(id1, allGames[1].gameId)
    }

    @Test
    fun testGetAllFlow() = runTest {
        val game1 = createTestGame("2020-01-01T00:00:00Z")
        val game2 = createTestGame("2021-01-01T00:00:00Z")
        dao.insertOne(game1)
        dao.insertOne(game2)

        // Collect the first emission from the flow.
        val allGamesFlow = dao.getAllFlow().first()
        assertEquals(2, allGamesFlow.size)
        // Verify descending order by startDate.
        assertEquals(Instant.parse("2021-01-01T00:00:00Z"), allGamesFlow.first().startDate)
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), allGamesFlow.last().startDate)
    }

    @Test
    fun testGetOneFlow() = runTest {
        val game = createTestGame("2020-06-01T00:00:00Z")
        val insertedId = dao.insertOne(game)

        // Collect the first emission from the flow for this game.
        val retrievedGame = dao.getOneFlow(insertedId).first()
        assertEquals(insertedId, retrievedGame.gameId)
        assertEquals(Instant.parse("2020-06-01T00:00:00Z"), retrievedGame.startDate)
    }

    @Test
    fun testUpdateOne() = runTest {
        val game = createTestGame("2020-01-01T00:00:00Z")
        val insertedId = dao.insertOne(game)

        // Update the game with a new startDate and a new gameName.
        val updatedGame = game.copy(
            gameId = insertedId,
            gameName = "Updated Game",
            startDate = Instant.parse("2022-01-01T00:00:00Z")
        )
        dao.updateOne(updatedGame)

        val retrievedGame = dao.getOne(insertedId)
        assertEquals("Updated Game", retrievedGame.gameName)
        assertEquals(Instant.parse("2022-01-01T00:00:00Z"), retrievedGame.startDate)
    }

    @Test
    fun testDeleteOne() = runTest {
        val game = createTestGame("2020-01-01T00:00:00Z")
        val insertedId = dao.insertOne(game)

        // Delete the inserted game.
        dao.deleteOne(insertedId)

        // Attempting to retrieve the deleted game should throw GameNotFoundException.
        assertFailsWith<GameNotFoundException> {
            dao.getOne(insertedId)
        }
    }

    @Test
    fun testGetOneNotFound() = runTest {
        // No game with id 999L has been inserted.
        assertFailsWith<GameNotFoundException> {
            dao.getOne(999L)
        }
    }

    @Test
    fun testGetOneFlowNotFound() = runTest {
        // Collecting from the flow for a non-existent game id should throw GameNotFoundException.
        assertFailsWith<GameNotFoundException> {
            dao.getOneFlow(999L).first()
        }
    }

    private fun createTestGame(startDateString: String): DbGame {
        return DbGame(
            gameId = NOT_SET_GAME_ID,
            gameName = "Test Game",
            nameP1 = "Alice",
            nameP2 = "Bob",
            nameP3 = "Charlie",
            nameP4 = "Diana",
            startDate = Instant.parse(startDateString),
            endDate = null
        )
    }
}