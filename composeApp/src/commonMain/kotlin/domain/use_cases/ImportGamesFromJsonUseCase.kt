package domain.use_cases

//import android.content.ContentResolver
//import android.net.Uri
//import screens.common.model.exceptions.JsonGamesNotValidException
import androidx.annotation.VisibleForTesting
import data.database.tables.DbGame
import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import domain.model.PortableGame
import domain.model.PortableRound
import domain.model.toDbGame
import domain.model.toDbRounds
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import ui.common.components.GameId

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