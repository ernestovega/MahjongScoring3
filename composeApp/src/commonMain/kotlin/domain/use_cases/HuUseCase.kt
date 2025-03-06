package domain.use_cases

import data.repositories.rounds.RoundsRepository
import domain.model.enums.TableWinds
import ui.common.components.RoundId

class HuUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(
        roundId: RoundId,
        winnerInitialSeat: TableWinds,
        discarderInitialSeat: TableWinds,
        points: Int,
    ): Result<Unit> =
        roundsRepository.getOne(roundId)
            .getOrThrow()
            .let { round ->
                val updatedRound = round.copy(
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = discarderInitialSeat,
                    handPoints = points.toLong(),
                )
                roundsRepository.updateOne(updatedRound)
                    .onSuccess { endRoundUseCase.invoke(round.gameId) }
            }
}