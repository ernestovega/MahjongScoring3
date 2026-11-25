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

/** In-memory repositories mimicking SQLite AUTOINCREMENT ids (never reused). */
class FakeGamesRepository : GamesRepository {
    private var lastId: GameId = 0
    val games = MutableStateFlow<List<GameRecord>>(emptyList())

    override fun getAllFlow(): Flow<List<GameRecord>> =
        games.map { list -> list.sortedWith(compareByDescending<GameRecord> { it.startDate }.thenByDescending { it.gameId }) }

    override fun getOneFlow(gameId: GameId): Flow<GameRecord?> = games.map { list -> list.find { it.gameId == gameId } }

    override suspend fun getOne(gameId: GameId): Result<GameRecord> =
        runCatching { games.value.first { it.gameId == gameId } }

    override suspend fun insertOne(gameRecord: GameRecord): Result<GameId> {
        val newId = ++lastId
        games.value = games.value + gameRecord.copy(gameId = newId)
        return Result.success(newId)
    }

    override suspend fun updateOne(gameRecord: GameRecord): Result<Boolean> {
        val exists = games.value.any { it.gameId == gameRecord.gameId }
        games.value = games.value.map { if (it.gameId == gameRecord.gameId) gameRecord else it }
        return Result.success(exists)
    }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> {
        val exists = games.value.any { it.gameId == gameId }
        games.value = games.value.filterNot { it.gameId == gameId }
        return Result.success(exists)
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

    override suspend fun insertOne(roundRecord: RoundRecord): Result<Boolean> {
        rounds.value = rounds.value + roundRecord.copy(roundId = ++lastId)
        return Result.success(true)
    }

    override suspend fun updateOne(roundRecord: RoundRecord): Result<Boolean> {
        val exists = rounds.value.any { it.roundId == roundRecord.roundId }
        rounds.value = rounds.value.map { if (it.roundId == roundRecord.roundId) roundRecord else it }
        return Result.success(exists)
    }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> {
        rounds.value = rounds.value.filterNot { it.gameId == gameId }
        return Result.success(true)
    }

    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> {
        val exists = rounds.value.any { it.roundId == roundId }
        rounds.value = rounds.value.filterNot { it.roundId == roundId }
        return Result.success(exists)
    }
}
