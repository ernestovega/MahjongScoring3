package domain.use_cases

import data.database.room.tables.DbGame
import data.repositories.games.GamesRepository
import domain.model.UiGame
import kotlinx.datetime.Clock

class EndGameUseCase(
    private val gamesRepository: GamesRepository,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        gamesRepository.updateOne(
            DbGame(
                gameId = uiGame.gameId,
                nameP1 = uiGame.nameP1,
                nameP2 = uiGame.nameP2,
                nameP3 = uiGame.nameP3,
                nameP4 = uiGame.nameP4,
                startDate = uiGame.startDate,
                endDate = Clock.System.now(),
                gameName = uiGame.gameName,
            )
        )
}
