package screens.common.use_cases

import screens.common.model.RoundId
import screens.common.data.rounds.DbRound
import screens.common.data.rounds.RoundsRepository
import screens.common.ui.NOT_SET_ROUND_ID

class DeleteRoundUseCase(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(roundId: RoundId): Result<Boolean> =
        roundsRepository.getOne(roundId)
            .mapCatching { dbRound ->
                roundsRepository.deleteOne(roundId)
                    .onSuccess {
                        if (dbRound.isOngoing()) {
                            roundsRepository.insertOne(
                                DbRound(
                                    gameId = dbRound.gameId,
                                    roundId = NOT_SET_ROUND_ID
                                )
                            )
                        }
                    }.getOrThrow()
            }
}
