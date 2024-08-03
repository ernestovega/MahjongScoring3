package screens.common.use_cases

import database.tables.DbRound
import screens.common.data.rounds.RoundsRepository
import screens.common.ui.NOT_SET_ROUND_ID
import screens.common.ui.RoundId

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
