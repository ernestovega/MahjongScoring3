package domain.model.exceptions

import com.etologic.mahjongscoring.common.components.GameId

class RoundsNotFoundException(val gameId: GameId) : Exception() {

    override val message: String
        get() = "GameId = $gameId -> message = ${super.message}"
}