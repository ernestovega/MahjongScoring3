package domain.model

import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NONE
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST
import domain.use_cases.utils.fourth
import domain.use_cases.utils.second
import domain.use_cases.utils.third
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ui.common.components.GameId
import ui.common.components.MIN_MCR_POINTS
import ui.common.components.NOT_SET_GAME_ID
import ui.common.components.NUM_NO_WINNER_PLAYERS
import ui.common.components.POINTS_DISCARD_NEUTRAL_PLAYERS
import ui.common.components.SeatState
import ui.common.components.SmallSeatsState

data class UiGame(
    val gameId: GameId,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
    val startDate: Instant,
    val endDate: Instant?,
    val gameName: String,
    val uiRounds: List<UiRound>,
) {
    val ongoingOrLastRound: UiRound = uiRounds.last()
    val isEnded: Boolean = endDate != null
    val playersNames: Array<String> = arrayOf(nameP1, nameP2, nameP3, nameP4)
    val endedUiRounds = uiRounds.filter { !it.isOngoing() }

    constructor() : this(
        gameId = NOT_SET_GAME_ID,
        nameP1 = "",
        nameP2 = "",
        nameP3 = "",
        nameP4 = "",
        startDate = Clock.System.now(),
        endDate = null,
        gameName = "",
        uiRounds = listOf(UiRound()),
    )

    init {

        uiRounds.forEachIndexed { index, uiRound ->
            val roundNumber = index + 1
            uiRound.roundNumber = roundNumber
        }

        val bestHand = getBestHand()

        uiRounds.forEach { uiRound ->
            val roundPoints = getRoundPoints(uiRound)
            uiRound.pointsP1 = roundPoints[0]
            uiRound.pointsP2 = roundPoints[1]
            uiRound.pointsP3 = roundPoints[2]
            uiRound.pointsP4 = roundPoints[3]
        }

        uiRounds.forEach { uiRound ->
            val roundTotals = getTotalPointsUntilRound(uiRound.roundNumber)
            uiRound.totalPointsP1 = roundTotals[0]
            uiRound.totalPointsP2 = roundTotals[1]
            uiRound.totalPointsP3 = roundTotals[2]
            uiRound.totalPointsP4 = roundTotals[3]

            val isBestHand = uiRound.roundNumber == bestHand.roundNumber
            uiRound.isBestHand = isBestHand
        }
    }

    private fun getRoundPoints(uiRound: UiRound): IntArray =
        with(uiRound) {
            when (winnerInitialSeat) {
                null,
                NONE -> intArrayOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)

                else -> if (discarderInitialSeat == null || discarderInitialSeat == NONE) {
                    intArrayOf(
                        penaltyP1 + calculateSelfPickSeatPoints(EAST, winnerInitialSeat, handPoints),
                        penaltyP2 + calculateSelfPickSeatPoints(SOUTH, winnerInitialSeat, handPoints),
                        penaltyP3 + calculateSelfPickSeatPoints(WEST, winnerInitialSeat, handPoints),
                        penaltyP4 + calculateSelfPickSeatPoints(NORTH, winnerInitialSeat, handPoints),
                    )
                } else {
                    intArrayOf(
                        penaltyP1 + calculateDiscardSeatPoints(EAST, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP2 + calculateDiscardSeatPoints(SOUTH, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP3 + calculateDiscardSeatPoints(WEST, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP4 + calculateDiscardSeatPoints(NORTH, winnerInitialSeat, discarderInitialSeat, handPoints),
                    )
                }
            }
        }

    private fun getTotalPointsUntilRound(roundNumber: Int): IntArray {
        val totalPoints = intArrayOf(0, 0, 0, 0)
        for (i in 0..<roundNumber) {
            totalPoints[0] += uiRounds[i].pointsP1
            totalPoints[1] += uiRounds[i].pointsP2
            totalPoints[2] += uiRounds[i].pointsP3
            totalPoints[3] += uiRounds[i].pointsP4
        }
        return totalPoints
    }

    private fun calculateSelfPickSeatPoints(
        seat: TableWinds,
        winnerInitialSeat: TableWinds,
        handPoints: Int,
    ): Int =
        if (seat == winnerInitialSeat) {
            getHuSelfPickWinnerPoints(handPoints)
        } else {
            getHuSelfPickDiscarderPoints(handPoints)
        }

    private fun calculateDiscardSeatPoints(
        seat: TableWinds,
        winnerInitialSeat: TableWinds,
        discarderInitialSeat: TableWinds,
        handPoints: Int,
    ): Int =
        when (seat) {
            winnerInitialSeat -> getHuDiscardWinnerPoints(handPoints)
            discarderInitialSeat -> getHuDiscardDiscarderPoints(handPoints)
            else -> POINTS_DISCARD_NEUTRAL_PLAYERS
        }

    fun getBestHand(): BestHand {
        var bestHand = BestHand()
        this.uiRounds
            .filter { it.winnerInitialSeat != null }
            .forEach { uiRound ->
                if (uiRound.handPoints > bestHand.handValue) {
                    bestHand = BestHand(
                        roundNumber = uiRound.roundNumber,
                        handValue = uiRound.handPoints,
                        playerInitialPosition = uiRound.winnerInitialSeat!!,
                        playerName = getPlayerNameByInitialPosition(uiRound.winnerInitialSeat)
                    )
                }
            }
        return bestHand
    }

    fun getPlayerNameByInitialPosition(initialPosition: TableWinds): String {
        return when (initialPosition) {
            NONE -> ""
            EAST -> nameP1
            SOUTH -> nameP2
            WEST -> nameP3
            NORTH -> nameP4
        }
    }

    fun getCurrentEastSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingOrLastRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[EAST.index]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[SOUTH.index]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[WEST.index]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[NORTH.index]
            else -> null
        }
    }

    fun getCurrentSouthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingOrLastRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[SOUTH.index]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[WEST.index]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[NORTH.index]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[EAST.index]
            else -> null
        }
    }

    fun getCurrentWestSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingOrLastRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[WEST.index]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[NORTH.index]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[EAST.index]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[SOUTH.index]
            else -> null
        }
    }

    fun getCurrentNorthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingOrLastRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[NORTH.index]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[EAST.index]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[SOUTH.index]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[WEST.index]
            else -> null
        }
    }

    fun getPlayersNamesByCurrentSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val currentRoundNumber = ongoingOrLastRound.roundNumber
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).index] = nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).index] = nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).index] = nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).index] = nameP4
        return namesListByCurrentSeat
    }

    fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val totalPointsByInitialSeat = intArrayOf(
            ongoingOrLastRound.totalPointsP1,
            ongoingOrLastRound.totalPointsP2,
            ongoingOrLastRound.totalPointsP3,
            ongoingOrLastRound.totalPointsP4,
        )
        return intArrayOf(
            totalPointsByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(EAST).index],
            totalPointsByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(SOUTH).index],
            totalPointsByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(WEST).index],
            totalPointsByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(NORTH).index],
        )
    }

    fun getOngoingOrLastRoundPlayersPenaltiesByCurrentSeat(): IntArray {
        val penaltiesByInitialSeat = intArrayOf(
            ongoingOrLastRound.penaltyP1,
            ongoingOrLastRound.penaltyP2,
            ongoingOrLastRound.penaltyP3,
            ongoingOrLastRound.penaltyP4,
        )
        return intArrayOf(
            penaltiesByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(EAST).index],
            penaltiesByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(SOUTH).index],
            penaltiesByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(WEST).index],
            penaltiesByInitialSeat[getPlayerInitialSeatByOngoingOrLastRoundSeat(NORTH).index],
        )
    }

    fun getTableDiffs(): TableDiffs =
        with(getPlayersTotalPointsByCurrentSeat()) {
            TableDiffs(
                eastSeatPoints = this[EAST.index],
                southSeatPoints = this[SOUTH.index],
                westSeatPoints = this[WEST.index],
                northSeatPoints = this[NORTH.index],
            )
        }

    private fun getInitialEastPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> EAST
        }

    private fun getInitialSouthPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> SOUTH
        }

    private fun getInitialWestPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
            else -> WEST
        }

    private fun getInitialNorthPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> NORTH
        }

    fun getPlayerInitialSeatByOngoingOrLastRoundSeat(currentSeatPosition: TableWinds): TableWinds =
        when (ongoingOrLastRound.roundNumber) {
            1, 2, 3, 4 -> getPlayerInitialPositionBySeatInRoundEast(currentSeatPosition)
            5, 6, 7, 8 -> getPlayerInitialPositionBySeatInRoundSouth(currentSeatPosition)
            9, 10, 11, 12 -> getPlayerInitialPositionBySeatInRoundWest(currentSeatPosition)
            13, 14, 15, 16 -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
            else -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
        }

    private fun getPlayerInitialPositionBySeatInRoundEast(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> EAST
            SOUTH -> SOUTH
            WEST -> WEST
            NORTH -> NORTH
            else -> NORTH
        }

    private fun getPlayerInitialPositionBySeatInRoundSouth(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> SOUTH
            SOUTH -> EAST
            WEST -> NORTH
            NORTH -> WEST
            else -> WEST
        }

    private fun getPlayerInitialPositionBySeatInRoundWest(seatPosition: TableWinds): TableWinds {
        return when (seatPosition) {
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> SOUTH
            NORTH -> EAST
            else -> EAST
        }
    }

    private fun getPlayerInitialPositionBySeatInRoundNorth(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> NORTH
            SOUTH -> WEST
            WEST -> EAST
            NORTH -> SOUTH
            else -> SOUTH
        }

    fun getSeatsCurrentWind(roundNumber: Int): Array<TableWinds> =
        when (roundNumber) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }

    fun getSeatsCurrentWind(): Array<TableWinds> =
        when (ongoingOrLastRound.roundNumber) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }

    companion object {
        fun getHuSelfPickWinnerPoints(huPoints: Int) =
            (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS

        fun getHuSelfPickDiscarderPoints(huPoints: Int) =
            -(huPoints + MIN_MCR_POINTS)

        fun getHuDiscardWinnerPoints(huPoints: Int) =
            huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)

        fun getHuDiscardDiscarderPoints(huPoints: Int) =
            -(huPoints + MIN_MCR_POINTS)

        fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) =
            penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}

fun UiGame.getCurrentSeatStates(): SmallSeatsState {
    val gameNames = this.getPlayersNamesByCurrentSeat()
    val gameTotalPoints = this.getPlayersTotalPointsByCurrentSeat()
    return SmallSeatsState(
        eastSeat = SeatState(
            wind = EAST,
            name = gameNames.first(),
            points = gameTotalPoints.first(),
        ),
        southSeat = SeatState(
            wind = SOUTH,
            name = gameNames.second(),
            points = gameTotalPoints.second(),
        ),
        westSeat = SeatState(
            wind = WEST,
            name = gameNames.third(),
            points = gameTotalPoints.third(),
        ),
        northSeat = SeatState(
            wind = NORTH,
            name = gameNames.fourth(),
            points = gameTotalPoints.fourth(),
        )
    )
}