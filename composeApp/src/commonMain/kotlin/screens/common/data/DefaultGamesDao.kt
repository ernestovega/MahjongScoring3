package screens.common.data

import screens.common.model.GameId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DefaultGamesDao: GamesDao {

    override fun getAllFlow(): Flow<List<DbGame>> = flowOf(listOf(DbGame(0)))

    override fun getOneFlow(gameId: GameId): Flow<DbGame> = flowOf(DbGame(gameId = gameId))

    override suspend fun getOne(gameId: GameId): DbGame = DbGame()

    override suspend fun insertOne(dbGame: DbGame): GameId = 0

    override suspend fun updateOne(dbGame: DbGame): Int = 1

    override suspend fun deleteOne(gameId: GameId): Int = 1
}
