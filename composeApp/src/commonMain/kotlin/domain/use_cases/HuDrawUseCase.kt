package domain.use_cases

import com.etologic.mahjongscoring.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.UiRound
import domain.model.enums.TableWinds.NONE
import ui.common.components.GameId

class HuDrawUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(uiRound: UiRound): Result<Unit> =
        with(uiRound) {
            val updatedRound = DbRound(
                gameId = this.gameId,
                roundId = this.roundId,
                winnerInitialSeat = NONE,
                discarderInitialSeat = NONE,
                handPoints = this.handPoints.toLong(),
                penaltyP1 = this.penaltyP1.toLong(),
                penaltyP2 = this.penaltyP2.toLong(),
                penaltyP3 = this.penaltyP3.toLong(),
                penaltyP4 = this.penaltyP4.toLong(),
            )
            roundsRepository.updateOne(updatedRound)
        }.onSuccess { endRoundUseCase.invoke(uiRound.gameId) }
}
