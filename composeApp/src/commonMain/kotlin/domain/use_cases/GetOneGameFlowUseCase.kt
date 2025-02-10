package domain.use_cases

import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import domain.model.UiGame
import domain.use_cases.mappers.toUiGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import ui.common.components.GameId

class GetOneGameFlowUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(gameId: GameId): Flow<UiGame> =
        combine(
            gamesRepository.getOneFlow(gameId),
            roundsRepository.getGameRoundsFlow(gameId),
        ) { dbGame, dbRounds ->
            if (dbRounds.isEmpty()) {
                null
            } else {
                dbGame.toUiGame(dbRounds)
            }
        }
            .filterNotNull()
}
