package screens.common.data

import screens.common.model.GameId
import screens.common.model.RoundId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class DefaultRoundsDao: RoundsDao {

    override fun getAllFlow(): Flow<List<DbRound>> = flowOf(listOf(DbRound(0, 0)))

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> = flowOf(listOf(DbRound(0, 0)))

    override suspend fun getGameRounds(gameId: GameId): List<DbRound> = listOf(DbRound(0, 0))

    override suspend fun getOne(roundId: RoundId): DbRound = DbRound(0, 0)

    override suspend fun insertOne(round: DbRound): RoundId = 0

    override suspend fun updateOne(round: DbRound): Int = 1

    override suspend fun deleteGameRounds(gameId: GameId): Int = 1

    override suspend fun deleteOne(roundId: RoundId): Int = 1
}
