package domain.use_cases

import data.database.tables.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.HuData
import domain.model.UiGame
import domain.model.enums.TableWinds.NONE

class HuSelfPickUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {

    suspend operator fun invoke(uiGame: UiGame, huData: HuData): Result<Boolean> =
        with(uiGame.ongoingOrLastRound) {
            roundsRepository.updateOne(
                DbRound(
                    gameId = this.gameId,
                    roundId = this.roundId,
                    winnerInitialSeat = huData.winnerInitialSeat,
                    discarderInitialSeat = NONE,
                    handPoints = huData.points,
                    penaltyP1 = this.penaltyP1,
                    penaltyP2 = this.penaltyP2,
                    penaltyP3 = this.penaltyP3,
                    penaltyP4 = this.penaltyP4,
                )
            )
        }
            .onSuccess { endRoundUseCase.invoke(uiGame.gameId) }
}
