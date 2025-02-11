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
    ): Result<Boolean> =
        roundsRepository.getOne(roundId)
            .getOrThrow()
            .let { round ->
                if (isDivided) {
                    val noPenalizedPlayerPoints = points / NUM_NO_WINNER_PLAYERS
                    round.penaltyP1 += if (EAST == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints
                    round.penaltyP2 += if (SOUTH == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints
                    round.penaltyP3 += if (WEST == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints
                    round.penaltyP4 += if (NORTH == penalizedPlayerInitialSeat) -points else noPenalizedPlayerPoints
                } else if (points > 0) {
                    round.penaltyP1 = if (penalizedPlayerInitialSeat == EAST) round.penaltyP1 - points else round.penaltyP1
                    round.penaltyP2 = if (penalizedPlayerInitialSeat == SOUTH) round.penaltyP2 - points else round.penaltyP2
                    round.penaltyP3 = if (penalizedPlayerInitialSeat == WEST) round.penaltyP3 - points else round.penaltyP3
                    round.penaltyP4 = if (penalizedPlayerInitialSeat == NORTH) round.penaltyP4 - points else round.penaltyP4
                } else if (penalizedPlayerInitialSeat == NONE){
                    round.penaltyP1 = 0
                    round.penaltyP2 = 0
                    round.penaltyP3 = 0
                    round.penaltyP4 = 0
                } else {
                    return@let Result.failure(IllegalArgumentException("Invalid penalty data"))
                }
                roundsRepository.updateOne(round)
            }
}
