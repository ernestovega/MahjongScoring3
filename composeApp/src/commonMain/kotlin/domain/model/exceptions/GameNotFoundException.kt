package domain.model.exceptions

import ui.common.components.GameId

class GameNotFoundException(val gameId: GameId) : Exception() {

    override val message: String
        get() = "GameId = $gameId -> message = ${super.message}"
}