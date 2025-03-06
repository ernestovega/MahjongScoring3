package data.repositories.games

import com.etologic.mahjongscoring.DbGame
import kotlinx.coroutines.flow.Flow

interface GamesDataSource {
    fun getAllFlow(): Flow<List<DbGame>>
    fun getOneFlow(gameId: Long): Flow<DbGame>
    suspend fun getAll(): List<DbGame>
    suspend fun getOne(gameId: Long): DbGame
    suspend fun insertOne(dbGame: DbGame): Long
    suspend fun updateOne(dbGame: DbGame)
    suspend fun deleteOne(gameId: Long)
}