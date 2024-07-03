package screens.common.data.rounds

import screens.common.model.GameId
import screens.common.model.RoundId
import screens.common.data.DbRound
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    fun getAllFlow(): Flow<List<DbRound>>
    fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>>
    suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>>
    suspend fun getOne(roundId: RoundId): Result<DbRound>
    suspend fun insertOne(dbRound: DbRound): Result<Boolean>
    suspend fun updateOne(dbRound: DbRound): Result<Boolean>
    suspend fun deleteGameRounds(gameId: GameId): Result<Boolean>
    suspend fun deleteOne(roundId: RoundId): Result<Boolean>
}