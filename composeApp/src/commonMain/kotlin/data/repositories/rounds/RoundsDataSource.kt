package data.repositories.rounds

import com.etologic.mahjongscoring.DbRound
import kotlinx.coroutines.flow.Flow

interface RoundsDataSource {
    fun getAllFlow(): Flow<List<DbRound>>
    fun getGameRoundsFlow(gameId: Long): Flow<List<DbRound>>
    suspend fun getGameRounds(gameId: Long): List<DbRound>
    suspend fun getOne(roundId: Long): DbRound
    suspend fun insertOne(round: DbRound): Long
    suspend fun updateOne(round: DbRound)
    suspend fun deleteGameRounds(gameId: Long)
    suspend fun deleteOne(roundId: Long)
}