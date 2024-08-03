package screens.common.use_cases

import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import screens.common.ui.GameId

class DeleteGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        roundsRepository.deleteGameRounds(gameId)
            .also { gamesRepository.deleteOne(gameId) }
}
