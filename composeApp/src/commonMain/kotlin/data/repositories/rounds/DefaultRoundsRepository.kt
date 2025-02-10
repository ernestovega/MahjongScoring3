package data.repositories.rounds

import data.database.daos.RoundsDao
import data.database.tables.DbRound
import kotlinx.coroutines.flow.Flow
import ui.common.components.GameId
import ui.common.components.RoundId

class DefaultRoundsRepository(private val roundsDao: RoundsDao) : RoundsRepository {

    override fun getAllFlow(): Flow<List<DbRound>> = roundsDao.getAllFlow()

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> = roundsDao.getGameRoundsFlow(gameId)

    override suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>> = runCatching { roundsDao.getGameRounds(gameId) }

    override suspend fun getOne(roundId: RoundId): Result<DbRound> = runCatching { roundsDao.getOne(roundId) }

    override suspend fun insertOne(dbRound: DbRound): Result<Boolean> = runCatching { roundsDao.insertOne(dbRound) > 0 }

    override suspend fun updateOne(dbRound: DbRound): Result<Boolean> = runCatching { roundsDao.updateOne(dbRound) == 1 }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching { roundsDao.deleteGameRounds(gameId) >= 0 }

    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> = runCatching { roundsDao.deleteOne(roundId) == 1 }
}
