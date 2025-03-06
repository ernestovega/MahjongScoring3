package data.repositories.rounds

import com.etologic.mahjongscoring.DbRound
import kotlinx.coroutines.flow.Flow
import ui.common.components.GameId
import ui.common.components.RoundId

class DefaultRoundsRepository(private val roundsDataSource: RoundsDataSource) : RoundsRepository {

    override fun getAllFlow(): Flow<List<DbRound>> =
        roundsDataSource.getAllFlow()

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> =
        roundsDataSource.getGameRoundsFlow(gameId)

    override suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>> =
        runCatching {
            roundsDataSource.getGameRounds(gameId)
        }

    override suspend fun getOne(roundId: RoundId): Result<DbRound> =
        runCatching {
            roundsDataSource.getOne(roundId)
        }

    override suspend fun insertOne(dbRound: DbRound): Result<Unit> =
        runCatching {
            roundsDataSource.insertOne(dbRound)
        }

    override suspend fun updateOne(dbRound: DbRound): Result<Unit> =
        runCatching {
            roundsDataSource.updateOne(dbRound)
        }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Unit> =
        runCatching {
            roundsDataSource.deleteGameRounds(gameId)
        }

    override suspend fun deleteOne(roundId: RoundId): Result<Unit> =
        runCatching {
            roundsDataSource.deleteOne(roundId)
        }
}
