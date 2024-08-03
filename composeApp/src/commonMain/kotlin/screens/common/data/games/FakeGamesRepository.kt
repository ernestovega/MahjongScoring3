package screens.common.data.games

import database.tables.DbGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import screens.common.model.exceptions.GameNotFoundException
import screens.common.ui.GameId
import kotlin.random.Random

class FakeGamesRepository : GamesRepository {

    private val fakeGames = listOf(
        DbGame(
            gameId = 7,
            nameP1 = "Carl",
            nameP2 = "Darlene",
            nameP3 = "Camille",
            nameP4 = "Maggie",
            startDate = Clock.System.now(),
            endDate = null,
            gameName = ""
        ),
        DbGame(
            gameId = 6,
            nameP1 = "Damien Bradley",
            nameP2 = "Kareem Frazier",
            nameP3 = "Tyson Pace",
            nameP4 = "Gabrielle McKee",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = "MMC '16 - R1 - T12"
        ),
        DbGame(
            gameId = 5,
            nameP1 = "Misty Shelton",
            nameP2 = "Desiree Ware",
            nameP3 = "Dennis Davis",
            nameP4 = "Guillermo Nash",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = "MMC '16 - R2 - T7"
        ),
        DbGame(
            gameId = 4,
            nameP1 = "Ruth Short",
            nameP2 = "Ella Chen",
            nameP3 = "Rosalind Mack",
            nameP4 = "Meagan Henson",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = "Mahjong Spain Open 2018"
        ),
        DbGame(
            gameId = 3,
            nameP1 = "Flossie Rasmussen",
            nameP2 = "Hiram Sutton",
            nameP3 = "Michelle David",
            nameP4 = "Wilbert Peterson",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = "European Mahjong Championship 2023"
        ),
        DbGame(
            gameId = 2,
            nameP1 = "Carlo England",
            nameP2 = "Darryl Henderson",
            nameP3 = "Angeline Evans",
            nameP4 = "Brenda Christensen",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = "Mahjong Madrid Championship 2019"
        ),
        DbGame(
            gameId = 1,
            nameP1 = "Yvette Bennett",
            nameP2 = "Elisa Lindsay",
            nameP3 = "Anton Baldwin",
            nameP4 = "Jacob Anderson",
            startDate = Clock.System.now(),
            endDate = Clock.System.now(),
            gameName = ""
        ),
    )

    override fun getAllFlow(): Flow<List<DbGame>> = flowOf(fakeGames)

    override fun getOneFlow(gameId: GameId): Flow<DbGame> = flowOf(fakeGames.find { it.gameId == gameId } ?: throw GameNotFoundException(gameId))

    override suspend fun getOne(gameId: GameId): Result<DbGame> = runCatching { fakeGames.find { it.gameId == gameId } ?: throw GameNotFoundException(gameId) }

    override suspend fun insertOne(dbGame: DbGame): Result<GameId> = runCatching { Random.nextLong(1, 6) }

    override suspend fun updateOne(dbGame: DbGame): Result<Boolean> = runCatching { true }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching { true }
}
