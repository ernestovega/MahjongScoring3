package domain.use_cases

import data.database.room.tables.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.exceptions.GameNotFoundException
import ui.common.components.GameId
import ui.common.components.MAX_MCR_ROUNDS
import ui.common.components.NOT_SET_ROUND_ID

class EndRoundUseCase(
    private val roundsRepository: RoundsRepository,
    private val endGameUseCase: EndGameUseCase,
    private val getOneGameUseCase: GetOneGameUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        runCatching {
            getOneGameUseCase.invoke(gameId)
                ?.let { uiGame ->
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
                ?: throw GameNotFoundException(gameId)
        }
}
