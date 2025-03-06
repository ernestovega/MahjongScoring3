package domain.use_cases

import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.DbRound
import data.repositories.games.GamesRepository
import data.repositories.rounds.RoundsRepository
import kotlinx.datetime.Clock
import ui.common.components.GameId
import ui.common.components.NOT_SET_GAME_ID
import ui.common.components.NOT_SET_ROUND_ID

class CreateGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    suspend operator fun invoke(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ): Result<GameId> {
        val newGame = DbGame(
            gameId = NOT_SET_GAME_ID,
            gameName = gameName,
            nameP1 = nameP1,
            nameP2 = nameP2,
            nameP3 = nameP3,
            nameP4 = nameP4,
            startDate = Clock.System.now(),
            endDate = null,
        )
        return gamesRepository.insertOne(newGame)
            .onSuccess { gameId ->
                roundsRepository.insertOne(
                    DbRound(
                        gameId = gameId,
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
    }
}
