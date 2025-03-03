package data

import data.database.daos.RoundsDao
import data.database.tables.DbRound
import domain.model.exceptions.RoundNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ui.common.components.GameId
import ui.common.components.RoundId

class FakeRoundsDao : RoundsDao {

    private val _rounds = MutableStateFlow<Map<RoundId, DbRound>>(emptyMap())
    private var _autoIncrementalId: Long = 1

    override fun getAllFlow(): Flow<List<DbRound>> =
        _rounds.map { it.values.toList() }

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> =
        _rounds.map { rounds ->
            rounds.values
                .filter { it.gameId == gameId }
                .toList()
        }

    override suspend fun getGameRounds(gameId: GameId): List<DbRound> =
        _rounds.value.values
            .filter { it.gameId == gameId }
            .toList()
            .sortedBy { it.roundId }

    override suspend fun getOne(roundId: RoundId): DbRound =
        (_rounds.value[roundId] ?: throw RoundNotFoundException(roundId))

    override suspend fun insertOne(dbRound: DbRound): RoundId {
        val newRoundId = _autoIncrementalId++
        val newRound = dbRound.copy(roundId = newRoundId)
        _rounds.update { rounds ->
            rounds.toMutableMap().apply {
                this[newRoundId] = newRound
            }
        }
        println("TEST --> FakeRoundsDao --> insertOne -->")
        _rounds.value.forEach { println(it) }
        return newRoundId
    }

    override suspend fun updateOne(dbRound: DbRound): Int {
        _rounds.update { rounds ->
            rounds.toMutableMap().apply {
                this[dbRound.roundId] = dbRound
            }
        }
        println("TEST --> FakeRoundsDao --> updateOne -->")
        _rounds.value.forEach { println(it) }
        return 1
    }

    override suspend fun deleteGameRounds(gameId: GameId): Int {
        var size = 0
        _rounds.update { rounds ->
            rounds.toMutableMap().apply {
                val gameRounds = rounds.filter { it.value.gameId == gameId }
                size = gameRounds.size
                gameRounds.forEach { remove(it.key) }
            }
        }
        println("TEST --> FakeRoundsDao --> deleteGameRounds -->")
        _rounds.value.forEach { println(it) }
        return size
    }

    override suspend fun deleteOne(roundId: RoundId): Int {
        _rounds.update { rounds ->
            rounds.toMutableMap().apply {
                remove(roundId)
            }
        }
        println("TEST --> FakeRoundsDao --> deleteOne -->")
        _rounds.value.forEach { println(it) }
        return 1
    }
}

