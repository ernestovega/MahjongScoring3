package data.repositories.games

import data.database.tables.DbGame
import kotlinx.coroutines.flow.Flow
import ui.common.components.GameId

interface GamesRepository {
    fun getAllFlow(): Flow<List<DbGame>>
    fun getOneFlow(gameId: GameId): Flow<DbGame>
    suspend fun getOne(gameId: GameId): Result<DbGame>
    suspend fun insertOne(dbGame: DbGame): Result<GameId>
    suspend fun updateOne(dbGame: DbGame): Result<Boolean>
    suspend fun deleteOne(gameId: GameId): Result<Boolean>
}