package screens.common.use_cases

import database.tables.DbRound
import screens.common.data.rounds.RoundsRepository
import screens.common.model.HuData
import screens.common.model.UiGame
import screens.common.model.enums.TableWinds.NONE

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
