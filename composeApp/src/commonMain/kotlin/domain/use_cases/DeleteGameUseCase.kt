package domain.use_cases

import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import ui.common.components.GameId

class DeleteGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        roundsRepository.deleteGameRounds(gameId)
            .also { gamesRepository.deleteOne(gameId) }
}
