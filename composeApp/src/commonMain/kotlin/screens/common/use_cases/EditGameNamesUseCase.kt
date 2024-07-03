package screens.common.use_cases

import screens.common.model.UiGame
import screens.common.data.DbGame
import screens.common.data.games.GamesRepository
import screens.common.use_cases.utils.normalizeName

class EditGameNamesUseCase(
    private val gamesRepository: GamesRepository,
) {
    suspend operator fun invoke(
        uiGame: UiGame,
        newGameName: String,
        newNameP1: String,
        newNameP2: String,
        newNameP3: String,
        newNameP4: String,
    ): Result<Boolean> =
        gamesRepository.updateOne(
            DbGame(
                gameId = uiGame.gameId,
                nameP1 = normalizeName(newNameP1),
                nameP2 = normalizeName(newNameP2),
                nameP3 = normalizeName(newNameP3),
                nameP4 = normalizeName(newNameP4),
                startDate = uiGame.startDate,
                endDate = uiGame.endDate,
                gameName = normalizeName(newGameName),
            )
        )
}
