package com.etologic.mahjongscoring3.domain

import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory repositories mimicking the Room DAOs (AUTOINCREMENT ids, explicit ids kept, ordering, return values).
 * Same behavior as Mahjong Scoring 2 testutil/FakeRepositories, so its tests can be ported as they are.
 */
class FakeGamesRepository : GamesRepository {
    private var lastId: GameId = 0
    val games = MutableStateFlow<List<GameRecord>>(emptyList())

    override fun getAllFlow(): Flow<List<GameRecord>> =
        games.map { list -> list.sortedWith(compareByDescending<GameRecord> { it.startDate }.thenByDescending { it.gameId }) }

    override fun getOneFlow(gameId: GameId): Flow<GameRecord?> = games.map { list -> list.find { it.gameId == gameId } }

    override suspend fun getOne(gameId: GameId): Result<GameRecord> =
        runCatching { games.value.first { it.gameId == gameId } }

    /** When not null, every write operation fails with this exception. */
    var failure: Throwable? = null

    override suspend fun insertOne(gameRecord: GameRecord): Result<GameId> = runCatching {
        failure?.let { throw it }
        val id = if (gameRecord.gameId == 0L) ++lastId else gameRecord.gameId.also { lastId = maxOf(lastId, it) }
        require(games.value.none { it.gameId == id }) { "UNIQUE constraint failed: Games.gameId" }
        games.value = games.value + gameRecord.copy(gameId = id)
        id
    }

    override suspend fun updateOne(gameRecord: GameRecord): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = games.value.any { it.gameId == gameRecord.gameId }
        if (exists) games.value = games.value.map { if (it.gameId == gameRecord.gameId) gameRecord else it }
        exists
    }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = games.value.any { it.gameId == gameId }
        games.value = games.value.filterNot { it.gameId == gameId }
        exists
    }
}

class FakeRoundsRepository : RoundsRepository {
    private var lastId: RoundId = 0
    val rounds = MutableStateFlow<List<RoundRecord>>(emptyList())

    override fun getAllFlow(): Flow<List<RoundRecord>> = rounds

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<RoundRecord>> =
        rounds.map { list -> list.filter { it.gameId == gameId } }

    override suspend fun getGameRounds(gameId: GameId): Result<List<RoundRecord>> =
        Result.success(rounds.value.filter { it.gameId == gameId }.sortedBy { it.roundId })

    override suspend fun getOne(roundId: RoundId): Result<RoundRecord> =
        runCatching { rounds.value.first { it.roundId == roundId } }

    var failure: Throwable? = null

    override suspend fun insertOne(roundRecord: RoundRecord): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val id = if (roundRecord.roundId == 0L) ++lastId else roundRecord.roundId.also { lastId = maxOf(lastId, it) }
        rounds.value = rounds.value + roundRecord.copy(roundId = id)
        id > 0
    }

    override suspend fun updateOne(roundRecord: RoundRecord): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = rounds.value.any { it.roundId == roundRecord.roundId }
        if (exists) rounds.value = rounds.value.map { if (it.roundId == roundRecord.roundId) roundRecord else it }
        exists
    }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        rounds.value = rounds.value.filterNot { it.gameId == gameId }
        true
    }

    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = rounds.value.any { it.roundId == roundId }
        rounds.value = rounds.value.filterNot { it.roundId == roundId }
        exists
    }
}
