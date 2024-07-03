package screens.common.use_cases

import kotlinx.coroutines.flow.first
import screens.common.data.DbRound
import screens.common.data.rounds.RoundsRepository
import screens.common.model.GameId
import screens.common.ui.MAX_MCR_ROUNDS
import screens.common.ui.NOT_SET_ROUND_ID

class EndRoundUseCase(
    private val roundsRepository: RoundsRepository,
    private val endGameUseCase: EndGameUseCase,
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        runCatching {
            getOneGameFlowUseCase.invoke(gameId)
                .first()
                .let { uiGame ->
                    if (uiGame.uiRounds.size < MAX_MCR_ROUNDS) {
                        roundsRepository.insertOne(
                            DbRound(
                                gameId = uiGame.gameId,
                                roundId = NOT_SET_ROUND_ID
                            )
                        )
                    } else {
                        endGameUseCase.invoke(uiGame)
                    }.getOrThrow()
                }
        }
}
