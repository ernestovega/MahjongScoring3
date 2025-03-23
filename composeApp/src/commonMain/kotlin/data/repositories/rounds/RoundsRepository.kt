package data.repositories.rounds

import com.etologic.mahjongscoring.DbRound
import kotlinx.coroutines.flow.Flow
import com.etologic.mahjongscoring.common.components.GameId
import com.etologic.mahjongscoring.common.components.RoundId

interface RoundsRepository {
    fun getAllFlow(): Flow<List<DbRound>>
    fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>>
    suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>>
    suspend fun getOne(roundId: RoundId): Result<DbRound>
    suspend fun insertOne(dbRound: DbRound): Result<Unit>
    suspend fun updateOne(dbRound: DbRound): Result<Unit>
    suspend fun deleteGameRounds(gameId: GameId): Result<Unit>
    suspend fun deleteOne(roundId: RoundId): Result<Unit>
}