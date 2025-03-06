package domain.use_cases

import com.etologic.mahjongscoring.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.UiRound
import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.NONE

class HuSelfPickUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(
        uiRound: UiRound,
        winnerInitialSeat: TableWinds,
        points: Int,
    ): Result<Unit> =
        with(uiRound) {
            roundsRepository.updateOne(
                DbRound(
                    gameId = this.gameId,
                    roundId = this.roundId,
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = NONE,
                    handPoints = points.toLong(),
                    penaltyP1 = this.penaltyP1.toLong(),
                    penaltyP2 = this.penaltyP2.toLong(),
                    penaltyP3 = this.penaltyP3.toLong(),
                    penaltyP4 = this.penaltyP4.toLong(),
                )
            )
        }
            .onSuccess { endRoundUseCase.invoke(uiRound.gameId) }
}
