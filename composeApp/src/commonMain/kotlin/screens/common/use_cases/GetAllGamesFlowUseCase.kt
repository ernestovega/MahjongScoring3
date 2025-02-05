package screens.common.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import screens.common.model.UiGame
import screens.common.use_cases.mappers.toUiGame

class GetAllGamesFlowUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(): Flow<List<UiGame>> =
        combine(
            gamesRepository.getAllFlow(),
            roundsRepository.getAllFlow(),
        ) { dbGames, dbRounds ->
            dbGames.map { dbGame ->
                val gameRounds = dbRounds.filter { round -> round.gameId == dbGame.gameId }
                if (gameRounds.isEmpty()) {
                    return@combine null
                } else {
                    dbGame.toUiGame(gameRounds)
                }
            }
        }
            .filterNotNull()
}
