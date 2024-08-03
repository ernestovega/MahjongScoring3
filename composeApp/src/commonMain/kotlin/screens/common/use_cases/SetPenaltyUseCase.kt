package screens.common.use_cases

import database.tables.DbRound
import screens.common.model.PenaltyData
import screens.common.model.UiGame
import screens.common.model.UiRound
import screens.common.model.enums.TableWinds
import screens.common.model.enums.TableWinds.EAST
import screens.common.model.enums.TableWinds.NORTH
import screens.common.model.enums.TableWinds.SOUTH
import screens.common.model.enums.TableWinds.WEST
import screens.common.data.rounds.RoundsRepository

class SetPenaltyUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound, penaltyData: PenaltyData): Result<Boolean> =
        roundsRepository.updateOne(
            if (penaltyData.isDivided) {
                getDbRoundApplyingAllPlayersPenaltyPoints(
                    uiRound,
                    penaltyData.penalizedPlayerInitialSeat,
                    penaltyData.points
                )
            } else {
                getDbRoundApplyingOnePlayerPenaltyPoints(
                    uiRound,
                    penaltyData.penalizedPlayerInitialSeat,
                    penaltyData.points
                )
            }
        )

    private fun getDbRoundApplyingAllPlayersPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialSeat: TableWinds,
        penaltyPoints: Int
    ): DbRound =
        with(uiRound) {
            UiGame.getPenaltyOtherPlayersPoints(penaltyPoints).let { noPenalizedPlayerPoints ->
                DbRound(
                    gameId = gameId,
                    roundId = roundId,
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = discarderInitialSeat,
                    handPoints = handPoints,
                    penaltyP1 = penaltyP1 + if (EAST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP2 = penaltyP2 + if (SOUTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP3 = penaltyP3 + if (WEST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP4 = penaltyP4 + if (NORTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                )
            }
        }

    private fun getDbRoundApplyingOnePlayerPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialPosition: TableWinds,
        penaltyPoints: Int
    ): DbRound =
        with(uiRound) {
            DbRound(
                gameId = gameId,
                roundId = roundId,
                winnerInitialSeat = winnerInitialSeat,
                discarderInitialSeat = discarderInitialSeat,
                handPoints = handPoints,
                penaltyP1 = if (penalizedPlayerInitialPosition == EAST) penaltyP1 - penaltyPoints else penaltyP1,
                penaltyP2 = if (penalizedPlayerInitialPosition == SOUTH) penaltyP2 - penaltyPoints else penaltyP2,
                penaltyP3 = if (penalizedPlayerInitialPosition == WEST) penaltyP3 - penaltyPoints else penaltyP3,
                penaltyP4 = if (penalizedPlayerInitialPosition == NORTH) penaltyP4 - penaltyPoints else penaltyP4,
            )
        }
}
