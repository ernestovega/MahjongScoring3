package data

import data.database.room.daos.GamesDao
import data.database.room.tables.DbGame
import domain.model.exceptions.GameNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ui.common.components.GameId
import kotlin.collections.set

class FakeGamesDao : GamesDao {

    private val _games = MutableStateFlow<Map<GameId, DbGame>>(emptyMap())
    private var _autoIncrementalId: Long = 1

    override fun getAllFlow(): Flow<List<DbGame>> =
        _games.map { games ->
            games.values
                .toList()
                .sortedByDescending { it.startDate }
        }

    override suspend fun getAll(): List<DbGame> =
        _games.value
            .values
            .toList()
            .sortedByDescending { it.startDate }

    override fun getOneFlow(gameId: GameId): Flow<DbGame> =
        _games.map { games ->
            games[gameId] ?: throw GameNotFoundException(gameId)
        }

    override suspend fun getOne(gameId: GameId): DbGame =
        _games.value[gameId] ?: throw GameNotFoundException(gameId)

    override suspend fun insertOne(dbGame: DbGame): GameId {
        val newGameId = _autoIncrementalId++
        val newGame = dbGame.copy(gameId = newGameId)
        _games.update { games ->
            games.toMutableMap().apply {
                this[newGameId] = newGame
            }
        }
        println("TEST --> FakeGamesDao --> insertOne -->")
        _games.value.forEach { println(it) }
        return newGameId
    }

    override suspend fun updateOne(dbGame: DbGame): Int {
        _games.update { games ->
            games.toMutableMap().apply {
                this[dbGame.gameId] = dbGame
            }
        }
        println("TEST --> FakeGamesDao --> updateOne -->")
        _games.value.forEach { println(it) }
        return 1
    }

    override suspend fun deleteOne(gameId: GameId): Int {
        _games.update { games ->
            games.toMutableMap().apply {
                remove(gameId)
            }
        }
        println("TEST --> FakeGamesDao --> deleteOne -->")
        _games.value.forEach { println(it) }
        return 1
    }
}