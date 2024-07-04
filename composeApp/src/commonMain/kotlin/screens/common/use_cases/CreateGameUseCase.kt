package screens.common.use_cases

import kotlinx.datetime.Clock
import screens.common.data.games.DbGame
import screens.common.data.rounds.DbRound
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import screens.common.model.GameId
import screens.common.ui.NOT_SET_GAME_ID
import screens.common.ui.NOT_SET_ROUND_ID

class CreateGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    suspend operator fun invoke(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ): Result<GameId> {
        val newGame = DbGame(
            gameId = NOT_SET_GAME_ID,
            gameName = gameName,
            nameP1 = nameP1,
            nameP2 = nameP2,
            nameP3 = nameP3,
            nameP4 = nameP4,
            startDate = Clock.System.now(),
            endDate = null,
        )
        return gamesRepository.insertOne(newGame)
            .onSuccess { gameId ->
                roundsRepository.insertOne(
                    DbRound(
                        gameId = gameId,
                        roundId = NOT_SET_ROUND_ID
                    )
                )
            }
    }
}
