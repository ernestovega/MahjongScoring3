package data.repositories.games

import data.database.daos.GamesDao
import data.database.tables.DbGame
import kotlinx.coroutines.flow.Flow
import ui.common.components.GameId

class DefaultGamesRepository(private var gamesDao: GamesDao) : GamesRepository {

    override fun getAllFlow(): Flow<List<DbGame>> = gamesDao.getAllFlow()

    override fun getOneFlow(gameId: GameId): Flow<DbGame> = gamesDao.getOneFlow(gameId)

    override suspend fun getOne(gameId: GameId): Result<DbGame> = runCatching { gamesDao.getOne(gameId) }

    override suspend fun insertOne(dbGame: DbGame): Result<GameId> = runCatching { gamesDao.insertOne(dbGame) }

    override suspend fun updateOne(dbGame: DbGame): Result<Boolean> = runCatching { gamesDao.updateOne(dbGame) == 1 }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching { gamesDao.deleteOne(gameId) == 1 }
}
