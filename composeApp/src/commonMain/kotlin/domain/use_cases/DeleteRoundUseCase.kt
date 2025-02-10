package domain.use_cases

import data.database.tables.DbRound
import data.repositories.rounds.RoundsRepository
import ui.common.components.NOT_SET_ROUND_ID
import ui.common.components.RoundId

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
