package domain.use_cases

import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import domain.model.UiGame
import domain.use_cases.mappers.toUiGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

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
