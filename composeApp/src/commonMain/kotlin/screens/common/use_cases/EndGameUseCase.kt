package screens.common.use_cases

import database.tables.DbGame
import kotlinx.datetime.Clock
import screens.common.data.games.GamesRepository
import screens.common.model.UiGame

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
