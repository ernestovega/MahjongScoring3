package com.etologic.mahjongscoring3.data.repositories

import com.etologic.mahjongscoring3.data.database.GamesDao
import com.etologic.mahjongscoring3.data.database.RoundsDao
import com.etologic.mahjongscoring3.data.database.toDb
import com.etologic.mahjongscoring3.data.database.toRecord
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameNotFoundException
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomGamesRepository(
    private val gamesDao: GamesDao,
) : GamesRepository {
    override fun getAllFlow(): Flow<List<GameRecord>> = gamesDao.getAllFlow().map { list -> list.map { it.toRecord() } }
    override fun getOneFlow(gameId: GameId): Flow<GameRecord?> = gamesDao.getOneFlow(gameId).map { it?.toRecord() }
    override suspend fun getOne(gameId: GameId): Result<GameRecord> =
        runCatching { gamesDao.getOne(gameId)?.toRecord() ?: throw GameNotFoundException(gameId) }

    override suspend fun insertOne(gameRecord: GameRecord): Result<GameId> = runCatching { gamesDao.insertOne(gameRecord.toDb()) }
    override suspend fun updateOne(gameRecord: GameRecord): Result<Boolean> = runCatching { gamesDao.updateOne(gameRecord.toDb()) == 1 }
    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching { gamesDao.deleteOne(gameId) == 1 }
}

class RoomRoundsRepository(
    private val roundsDao: RoundsDao,
) : RoundsRepository {
    override fun getAllFlow(): Flow<List<RoundRecord>> = roundsDao.getAllFlow().map { list -> list.map { it.toRecord() } }
    override fun getGameRoundsFlow(gameId: GameId): Flow<List<RoundRecord>> =
        roundsDao.getGameRoundsFlow(gameId).map { list -> list.map { it.toRecord() } }

    override suspend fun getGameRounds(gameId: GameId): Result<List<RoundRecord>> =
        runCatching { roundsDao.getGameRounds(gameId).map { it.toRecord() } }

    override suspend fun getOne(roundId: RoundId): Result<RoundRecord> =
        runCatching { roundsDao.getOne(roundId)?.toRecord() ?: throw NoSuchElementException("Round $roundId not found") }

    override suspend fun insertOne(roundRecord: RoundRecord): Result<Boolean> = runCatching { roundsDao.insertOne(roundRecord.toDb()) > 0 }
    override suspend fun updateOne(roundRecord: RoundRecord): Result<Boolean> = runCatching { roundsDao.updateOne(roundRecord.toDb()) == 1 }
    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching { roundsDao.deleteGameRounds(gameId) >= 0 }
    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> = runCatching { roundsDao.deleteOne(roundId) == 1 }
}
