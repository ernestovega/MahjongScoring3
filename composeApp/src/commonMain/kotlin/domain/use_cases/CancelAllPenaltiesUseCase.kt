package domain.use_cases

import com.etologic.mahjongscoring.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.UiRound

class CancelAllPenaltiesUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound): Result<Unit> =
        roundsRepository.updateOne(
            DbRound(
                gameId = uiRound.gameId,
                roundId = uiRound.roundId,
                winnerInitialSeat = uiRound.winnerInitialSeat,
                discarderInitialSeat = uiRound.discarderInitialSeat,
                handPoints = uiRound.handPoints.toLong(),
                penaltyP1 = 0,
                penaltyP2 = 0,
                penaltyP3 = 0,
                penaltyP4 = 0,
            )
        )
}
