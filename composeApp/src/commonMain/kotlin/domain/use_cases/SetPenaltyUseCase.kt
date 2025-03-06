package domain.use_cases

import com.etologic.mahjongscoring.DbRound
import data.repositories.rounds.RoundsRepository
import domain.model.UiRound
import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST

class SetPenaltyUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(
        uiRound: UiRound, 
        isDivided: Boolean,
        penalizedPlayerInitialSeat: TableWinds,
        points: Int,
    ): Result<Unit> =
        roundsRepository.updateOne(
            if (isDivided) {
                getDbRoundApplyingAllPlayersPenaltyPoints(uiRound, penalizedPlayerInitialSeat, points)
            } else {
                getDbRoundApplyingOnePlayerPenaltyPoints(uiRound, penalizedPlayerInitialSeat, points)
            }
        )

    private fun getDbRoundApplyingAllPlayersPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialSeat: TableWinds,
        penaltyPoints: Int,
    ): DbRound =
        with(uiRound) {
            domain.model.UiGame.getPenaltyOtherPlayersPoints(penaltyPoints).let { noPenalizedPlayerPoints ->
                DbRound(
                    gameId = gameId,
                    roundId = roundId,
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = discarderInitialSeat,
                    handPoints = handPoints.toLong(),
                    penaltyP1 = (penaltyP1 + if (EAST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints).toLong(),
                    penaltyP2 = (penaltyP2 + if (SOUTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints).toLong(),
                    penaltyP3 = (penaltyP3 + if (WEST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints).toLong(),
                    penaltyP4 = (penaltyP4 + if (NORTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints).toLong(),
                )
            }
        }

    private fun getDbRoundApplyingOnePlayerPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialPosition: TableWinds,
        penaltyPoints: Int,
    ): DbRound =
        with(uiRound) {
            DbRound(
                gameId = gameId,
                roundId = roundId,
                winnerInitialSeat = winnerInitialSeat,
                discarderInitialSeat = discarderInitialSeat,
                handPoints = handPoints.toLong(),
                penaltyP1 = (if (penalizedPlayerInitialPosition == EAST) penaltyP1 - penaltyPoints else penaltyP1).toLong(),
                penaltyP2 = (if (penalizedPlayerInitialPosition == SOUTH) penaltyP2 - penaltyPoints else penaltyP2).toLong(),
                penaltyP3 = (if (penalizedPlayerInitialPosition == WEST) penaltyP3 - penaltyPoints else penaltyP3).toLong(),
                penaltyP4 = (if (penalizedPlayerInitialPosition == NORTH) penaltyP4 - penaltyPoints else penaltyP4).toLong(),
            )
        }
}
