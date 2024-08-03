package screens.common.data.games

import database.tables.DbGame
import kotlinx.coroutines.flow.Flow
import screens.common.ui.GameId

interface GamesRepository {
    fun getAllFlow(): Flow<List<DbGame>>
    fun getOneFlow(gameId: GameId): Flow<DbGame>
    suspend fun getOne(gameId: GameId): Result<DbGame>
    suspend fun insertOne(dbGame: DbGame): Result<GameId>
    suspend fun updateOne(dbGame: DbGame): Result<Boolean>
    suspend fun deleteOne(gameId: GameId): Result<Boolean>
}