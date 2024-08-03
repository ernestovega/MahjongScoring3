
package screens.common.model

import screens.common.model.enums.TableWinds
import screens.common.ui.GameId
import screens.common.ui.NOT_SET_GAME_ID
import screens.common.ui.NOT_SET_ROUND_ID
import screens.common.ui.RoundId

data class UiRound(
    val gameId: GameId,
    val roundId: RoundId,
    val winnerInitialSeat: TableWinds?,
    val discarderInitialSeat: TableWinds?,
    val handPoints: Int,
    val penaltyP1: Int,
    val penaltyP2: Int,
    val penaltyP3: Int,
    val penaltyP4: Int,
) {
    var roundNumber: Int = 0
    var pointsP1: Int = 0
    var pointsP2: Int = 0
    var pointsP3: Int = 0
    var pointsP4: Int = 0
    var totalPointsP1: Int = 0
    var totalPointsP2: Int = 0
    var totalPointsP3: Int = 0
    var totalPointsP4: Int = 0
    var isBestHand: Boolean = false

    constructor() : this(
        gameId = NOT_SET_GAME_ID,
        roundId = NOT_SET_ROUND_ID,
        winnerInitialSeat = null,
        discarderInitialSeat = null,
        handPoints = 0,
        penaltyP1 = 0,
        penaltyP2 = 0,
        penaltyP3 = 0,
        penaltyP4 = 0,
    )

    val areTherePenalties: Boolean get() = penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0

    fun isOngoing(): Boolean = winnerInitialSeat == null
}
