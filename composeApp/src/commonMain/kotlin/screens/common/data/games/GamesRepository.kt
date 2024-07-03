package screens.common.data.games

import screens.common.model.GameId
import screens.common.data.DbGame
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
    fun getAllFlow(): Flow<List<DbGame>>
    fun getOneFlow(gameId: GameId): Flow<DbGame>
    suspend fun getOne(gameId: GameId): Result<DbGame>
    suspend fun insertOne(dbGame: DbGame): Result<GameId>
    suspend fun updateOne(dbGame: DbGame): Result<Boolean>
    suspend fun deleteOne(gameId: GameId): Result<Boolean>
}