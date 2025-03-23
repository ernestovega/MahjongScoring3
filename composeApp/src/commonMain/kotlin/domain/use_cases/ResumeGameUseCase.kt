package domain.use_cases

import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.DbRound
import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import domain.model.UiGame
import domain.model.exceptions.GameRoundsNumberExceededException
import com.etologic.mahjongscoring.common.components.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring.common.components.NOT_SET_ROUND_ID

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
                    roundId = NOT_SET_ROUND_ID,
                    winnerInitialSeat = null,
                    discarderInitialSeat = null,
                    handPoints = 0,
                    penaltyP1 = 0,
                    penaltyP2 = 0,
                    penaltyP3 = 0,
                    penaltyP4 = 0,
                )
            ).getOrThrow()
            true
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
