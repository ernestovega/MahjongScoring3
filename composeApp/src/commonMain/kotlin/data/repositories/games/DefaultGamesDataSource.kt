package data.repositories.games

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.MS3Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultGamesDataSource(private val db: MS3Database) : GamesDataSource {

    private val queries = db.dbGameQueries

    override fun getAllFlow(): Flow<List<DbGame>> =
        queries.getAllGames()
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun getAll(): List<DbGame> =
        withContext(Dispatchers.IO) {
            queries.getAllGames().executeAsList()
        }

    override fun getOneFlow(gameId: Long): Flow<DbGame> =
        queries.getGameById(gameId)
            .asFlow()
            .map { println("DefaultGamesDataSource -> getOneFlow($gameId) -> $it"); it}
            .mapToOneNotNull(Dispatchers.IO)

    override suspend fun getOne(gameId: Long): DbGame =
        withContext(Dispatchers.IO) {
            queries.getGameById(gameId).executeAsOne()
        }

    override suspend fun insertOne(dbGame: DbGame): Long =
        withContext(Dispatchers.IO) {
            queries.insertOne(
                gameName = dbGame.gameName,
                nameP1 = dbGame.nameP1,
                nameP2 = dbGame.nameP2,
                nameP3 = dbGame.nameP3,
                nameP4 = dbGame.nameP4,
                startDate = dbGame.startDate,
                endDate = dbGame.endDate,
            )
            queries.lastInsertRowId().executeAsOne()
        }

    override suspend fun updateOne(dbGame: DbGame) {
        withContext(Dispatchers.IO) {
            queries.updateOne(
                gameId = dbGame.gameId,
                gameName = dbGame.gameName,
                nameP1 = dbGame.nameP1,
                nameP2 = dbGame.nameP2,
                nameP3 = dbGame.nameP3,
                nameP4 = dbGame.nameP4,
                startDate = dbGame.startDate,
                endDate = dbGame.endDate,
            )
        }
    }

    override suspend fun deleteOne(gameId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteOne(gameId)
        }
    }
}