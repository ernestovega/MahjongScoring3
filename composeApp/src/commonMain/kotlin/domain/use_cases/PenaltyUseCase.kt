package domain.use_cases

import data.repositories.rounds.RoundsRepository
import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NONE
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST
import ui.common.components.NUM_NO_WINNER_PLAYERS
import ui.common.components.RoundId

class PenaltyUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(
        roundId: RoundId,
        points: Int,
        isDivided: Boolean,
        penalizedPlayerInitialSeat: TableWinds,
    ): Result<Unit> =
        roundsRepository.getOne(roundId)
            .getOrThrow()
            .let { round ->
                val updatedRound = if (isDivided) {
                    val noPenalizedPlayerPoints = points / NUM_NO_WINNER_PLAYERS
                    round.copy(
                        penaltyP1 = round.penaltyP1 + if (EAST == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints,
                        penaltyP2 = round.penaltyP2 + if (SOUTH == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints,
                        penaltyP3 = round.penaltyP3 + if (WEST == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints,
                        penaltyP4 = round.penaltyP4 + if (NORTH == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints,
                    )
                } else if (points > 0) {
                    round.copy(
                        penaltyP1 = if (penalizedPlayerInitialSeat == EAST) round.penaltyP1 - points else round.penaltyP1,
                        penaltyP2 = if (penalizedPlayerInitialSeat == SOUTH) round.penaltyP2 - points else round.penaltyP2,
                        penaltyP3 = if (penalizedPlayerInitialSeat == WEST) round.penaltyP3 - points else round.penaltyP3,
                        penaltyP4 = if (penalizedPlayerInitialSeat == NORTH) round.penaltyP4 - points else round.penaltyP4,
                    )
                } else if (penalizedPlayerInitialSeat == NONE) {
                    round.copy(
                        penaltyP1 = 0,
                        penaltyP2 = 0,
                        penaltyP3 = 0,
                        penaltyP4 = 0,
                    )
                } else {
                    return@let Result.failure(IllegalArgumentException("Invalid penalty data"))
                }
                roundsRepository.updateOne(updatedRound)
            }
}
