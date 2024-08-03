package screens.common.model.exceptions

import screens.common.ui.GameId

class RoundsNotFoundException(val gameId: GameId) : Exception() {

    override val message: String
        get() = "GameId = $gameId -> message = ${super.message}"
}