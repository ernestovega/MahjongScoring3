package screens.common.use_cases

import screens.common.model.UiGame
import screens.common.model.GameRoundsNumberExceededException
import screens.common.data.games.DbGame
import screens.common.data.rounds.DbRound
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.RoundsRepository
import screens.common.ui.MAX_MCR_ROUNDS
import screens.common.ui.NOT_SET_ROUND_ID

class ResumeGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        runCatching {
            if (uiGame.uiRounds.size < MAX_MCR_ROUNDS) {
                removeEndDateFromGame(uiGame)
                insertOngoingRoundIfNotPresent(uiGame)
            } else {
                throw GameRoundsNumberExceededException()
            }
        }

    private suspend fun insertOngoingRoundIfNotPresent(uiGame: UiGame): Boolean =
        if (uiGame.uiRounds.last().isOngoing().not()) {
            roundsRepository.insertOne(
                DbRound(
                    gameId = uiGame.gameId,
                    roundId = NOT_SET_ROUND_ID
                )
            ).getOrThrow()
        } else {
            false
        }

    private suspend fun removeEndDateFromGame(uiGame: UiGame) {
        gamesRepository.updateOne(
            DbGame(
                gameId = uiGame.gameId,
                nameP1 = uiGame.nameP1,
                nameP2 = uiGame.nameP2,
                nameP3 = uiGame.nameP3,
                nameP4 = uiGame.nameP4,
                startDate = uiGame.startDate,
                endDate = null,
                gameName = uiGame.gameName,
            )
        )
    }
}
