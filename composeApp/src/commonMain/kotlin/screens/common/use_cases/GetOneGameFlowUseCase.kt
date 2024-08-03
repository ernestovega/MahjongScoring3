package screens.common.use_cases

import screens.common.model.UiGame
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import screens.common.ui.GameId
import screens.common.use_cases.mappers.toUiGame

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
