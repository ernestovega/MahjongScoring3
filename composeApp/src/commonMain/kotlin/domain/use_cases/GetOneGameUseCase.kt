package domain.use_cases

import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import domain.model.UiGame
import domain.use_cases.mappers.toUiGame
import ui.common.components.GameId

class GetOneGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(gameId: GameId): UiGame? {
        val dbGame = gamesRepository.getOne(gameId).getOrNull()
        val dbRounds = roundsRepository.getGameRounds(gameId).getOrNull()
        return if (dbGame != null && !dbRounds.isNullOrEmpty()) dbGame.toUiGame(dbRounds) else null
    }
}