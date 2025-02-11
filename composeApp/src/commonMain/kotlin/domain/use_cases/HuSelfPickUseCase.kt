package domain.use_cases

import data.database.tables.DbRound
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
    ): Result<Boolean> =
        with(uiRound) {
            roundsRepository.updateOne(
                DbRound(
                    gameId = this.gameId,
                    roundId = this.roundId,
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = NONE,
                    handPoints = points,
                    penaltyP1 = this.penaltyP1,
                    penaltyP2 = this.penaltyP2,
                    penaltyP3 = this.penaltyP3,
                    penaltyP4 = this.penaltyP4,
                )
            )
        }
            .onSuccess { endRoundUseCase.invoke(uiRound.gameId) }
}
