package screens.common.use_cases

import database.tables.DbRound
import screens.common.data.rounds.RoundsRepository
import screens.common.model.UiRound

class CancelAllPenaltiesUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound): Result<Boolean> =
        roundsRepository.updateOne(
            DbRound(
                gameId = uiRound.gameId,
                roundId = uiRound.roundId,
                winnerInitialSeat = uiRound.winnerInitialSeat,
                discarderInitialSeat = uiRound.discarderInitialSeat,
                handPoints = uiRound.handPoints,
                penaltyP1 = 0,
                penaltyP2 = 0,
                penaltyP3 = 0,
                penaltyP4 = 0,
            )
        )
}
