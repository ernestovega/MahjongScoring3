package com.etologic.mahjongscoring3.domain.usecases

import com.etologic.mahjongscoring3.domain.model.HuData
import com.etologic.mahjongscoring3.domain.model.PenaltyData
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiRound
import com.etologic.mahjongscoring3.domain.model.toRoundRecord
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository

class HuDiscardUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(uiGame: UiGame, huData: HuData): Result<Boolean> =
        roundsRepository.updateOne(
            uiGame.ongoingRound.toRoundRecord().copy(
                winnerInitialSeat = huData.winnerInitialSeat,
                discarderInitialSeat = huData.discarderInitialSeat,
                handPoints = huData.points,
            )
        ).mapCatching { endRoundUseCase(uiGame.gameId).getOrThrow() }
}

class HuSelfPickUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(uiGame: UiGame, huData: HuData): Result<Boolean> =
        roundsRepository.updateOne(
            uiGame.ongoingRound.toRoundRecord().copy(
                winnerInitialSeat = huData.winnerInitialSeat,
                discarderInitialSeat = NONE,
                handPoints = huData.points,
            )
        ).mapCatching { endRoundUseCase(uiGame.gameId).getOrThrow() }
}

class HuDrawUseCase(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        roundsRepository.updateOne(
            uiGame.ongoingRound.toRoundRecord().copy(
                winnerInitialSeat = NONE,
                discarderInitialSeat = NONE,
            )
        ).mapCatching { endRoundUseCase(uiGame.gameId).getOrThrow() }
}

class DeleteRoundUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(roundId: RoundId): Result<Boolean> =
        roundsRepository.getOne(roundId)
            .mapCatching { roundRecord ->
                roundsRepository.deleteOne(roundId)
                    .onSuccess {
                        if (roundRecord.isOngoing()) {
                            roundsRepository.insertOne(RoundRecord(gameId = roundRecord.gameId))
                        }
                    }.getOrThrow()
            }
}

class SetPenaltyUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound, penaltyData: PenaltyData): Result<Boolean> =
        roundsRepository.updateOne(
            if (penaltyData.isDivided) {
                applyAllPlayersPenaltyPoints(uiRound, penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
            } else {
                applyOnePlayerPenaltyPoints(uiRound, penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
            }
        )

    private fun applyAllPlayersPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialSeat: TableWinds,
        penaltyPoints: Int,
    ): RoundRecord =
        with(uiRound) {
            val noPenalizedPlayerPoints = UiGame.getPenaltyOtherPlayersPoints(penaltyPoints)
            toRoundRecord().copy(
                penaltyP1 = penaltyP1 + if (EAST == penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                penaltyP2 = penaltyP2 + if (SOUTH == penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                penaltyP3 = penaltyP3 + if (WEST == penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                penaltyP4 = penaltyP4 + if (NORTH == penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
            )
        }

    private fun applyOnePlayerPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialSeat: TableWinds,
        penaltyPoints: Int,
    ): RoundRecord =
        with(uiRound) {
            toRoundRecord().copy(
                penaltyP1 = if (penalizedPlayerInitialSeat == EAST) penaltyP1 - penaltyPoints else penaltyP1,
                penaltyP2 = if (penalizedPlayerInitialSeat == SOUTH) penaltyP2 - penaltyPoints else penaltyP2,
                penaltyP3 = if (penalizedPlayerInitialSeat == WEST) penaltyP3 - penaltyPoints else penaltyP3,
                penaltyP4 = if (penalizedPlayerInitialSeat == NORTH) penaltyP4 - penaltyPoints else penaltyP4,
            )
        }
}

class CancelAllPenaltiesUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound): Result<Boolean> =
        roundsRepository.updateOne(
            uiRound.toRoundRecord().copy(
                penaltyP1 = 0,
                penaltyP2 = 0,
                penaltyP3 = 0,
                penaltyP4 = 0,
            )
        )
}
