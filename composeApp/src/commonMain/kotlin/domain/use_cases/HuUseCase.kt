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
    ): Result<Boolean> =
        roundsRepository.getOne(roundId)
            .getOrThrow()
            .let { round ->
                round.winnerInitialSeat = winnerInitialSeat
                round.discarderInitialSeat = discarderInitialSeat
                round.handPoints = points
                roundsRepository.updateOne(round)
                    .onSuccess { endRoundUseCase.invoke(round.gameId) }
            }
}