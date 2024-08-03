package screens.common.use_cases

//import android.content.ContentResolver
//import android.net.Uri
import androidx.annotation.VisibleForTesting
import database.tables.DbGame
import screens.common.model.PortableGame
import screens.common.model.PortableRound
import screens.common.model.toDbGame
import screens.common.model.toDbRounds
//import screens.common.model.exceptions.JsonGamesNotValidException
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import screens.common.ui.GameId

class ImportGamesFromJsonUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val deleteGameUseCase: DeleteGameUseCase,
) {
//    suspend operator fun invoke(uri: Uri, getContentResolver: () -> ContentResolver): Result<List<GameId>> =
//        runCatching {
//            val jsonText = getContentResolver().openInputStream(uri)?.bufferedReader()?.use { it.readText() }
//                ?: throw JsonGamesNotValidException()
//            createGamesInDbFrom(jsonText)
//        }

    @VisibleForTesting
    suspend fun createGamesInDbFrom(jsonText: String): List<GameId> {
        val portableGames = Json.decodeFromString(ListSerializer(PortableGame.serializer()), jsonText)
        return portableGames.map { portableGame -> createGameInDb(portableGame) }
    }

    private suspend fun createGameInDb(portableGame: PortableGame) =
        gamesRepository.insertOne(DbGame())
            .getOrThrow()
            .also { gameId ->
                createDbGameInDb(gameId, portableGame)
                createDbRoundsInDb(gameId, portableGame.rounds)
            }

    private suspend fun createDbGameInDb(gameId: GameId, portableGame: PortableGame) {
        portableGame.toDbGame(gameId).let { dbGame ->
            gamesRepository.updateOne(dbGame)
                .onFailure { deleteGameUseCase.invoke(gameId) }
        }
    }

    private suspend fun createDbRoundsInDb(gameId: GameId, portableRounds: List<PortableRound>) {
        portableRounds.toDbRounds(gameId)
            .forEach { dbRound ->
                roundsRepository.insertOne(dbRound)
                    .onFailure { deleteGameUseCase.invoke(gameId) }
            }
    }
}