package domain.model.exceptions

import ui.common.components.GameId

class RoundsNotFoundException(val gameId: GameId) : Exception() {

    override val message: String
        get() = "GameId = $gameId -> message = ${super.message}"
}