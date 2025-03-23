package data.repositories.games

import com.etologic.mahjongscoring.DbGame
import kotlinx.coroutines.flow.Flow
import com.etologic.mahjongscoring.common.components.GameId

class DefaultGamesRepository(private var gamesDataSource: GamesDataSource) : GamesRepository {

    override fun getAllFlow(): Flow<List<DbGame>> =
        gamesDataSource.getAllFlow()

    override fun getOneFlow(gameId: GameId): Flow<DbGame> =
        gamesDataSource.getOneFlow(gameId)

    override suspend fun getOne(gameId: GameId): Result<DbGame> =
        runCatching {
            gamesDataSource.getOne(gameId)
        }

    override suspend fun insertOne(dbGame: DbGame): Result<GameId> =
        runCatching {
            gamesDataSource.insertOne(dbGame)
        }

    override suspend fun updateOne(dbGame: DbGame): Result<Unit> =
        runCatching {
            gamesDataSource.updateOne(dbGame)
        }

    override suspend fun deleteOne(gameId: GameId): Result<Unit> =
        runCatching {
            gamesDataSource.deleteOne(gameId)
        }
}
