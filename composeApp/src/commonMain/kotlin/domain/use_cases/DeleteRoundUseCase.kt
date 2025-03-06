package domain.use_cases

import com.etologic.mahjongscoring.DbRound
import data.repositories.rounds.RoundsRepository
import ui.common.components.NOT_SET_ROUND_ID
import ui.common.components.RoundId

class DeleteRoundUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(roundId: RoundId): Result<Unit> =
        roundsRepository.getOne(roundId)
            .mapCatching { dbRound ->
                roundsRepository.deleteOne(roundId)
                    .onSuccess {
                        if (dbRound.isOnGoing()) {
                            roundsRepository.insertOne(
                                DbRound(
                                    gameId = dbRound.gameId,
                                    roundId = NOT_SET_ROUND_ID,
                                    winnerInitialSeat = null,
                                    discarderInitialSeat = null,
                                    handPoints = 0,
                                    penaltyP1 = 0,
                                    penaltyP2 = 0,
                                    penaltyP3 = 0,
                                    penaltyP4 = 0,
                                )
                            )
                        }
                    }.getOrThrow()
            }
}

fun DbRound.isOnGoing(): Boolean = winnerInitialSeat == null