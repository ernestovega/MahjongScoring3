package ui.screens.game

import domain.model.enums.TableWinds
import domain.model.getCurrentSeatStates
import domain.use_cases.GetOneGameFlowUseCase
import domain.use_cases.utils.fourth
import domain.use_cases.utils.second
import domain.use_cases.utils.third
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.penalties
import mahjongscoring3.composeapp.generated.resources.totals
import ui.common.BaseViewModel
import ui.common.components.GameId
import ui.common.components.NOT_SET_GAME_ID

class GameScreenViewModel(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) : BaseViewModel() {

    private val _gameIdFlow = MutableStateFlow(NOT_SET_GAME_ID)

    fun setGameId(gameId: GameId) {
        _gameIdFlow.update { gameId }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenStateFlow: StateFlow<GameScreenState> =
        _gameIdFlow
            .flatMapLatest(getOneGameFlowUseCase::invoke)
            .map { game ->
                val gameNames = game.getPlayersNamesByCurrentSeat()
                val gameTotalPoints = game.getPlayersTotalPointsByCurrentSeat()
                GameScreenState(
                    gamePageTableState = GamePageTableState(
                        gameName = game.gameName,
                        smallSeatsState = game.getCurrentSeatStates(),
                    ),
                    gamePageListState = GamePageListState(
                        gamePageListHeaderState = GamePageListHeaderState(
                            playerNameEastSeat = gameNames.first(),
                            playerNameSouthSeat = gameNames.second(),
                            playerNameWestSeat = gameNames.third(),
                            playerNameNorthSeat = gameNames.fourth(),
                        ),
                        roundsStates = game.endedUiRounds.map { round ->
                            val areTherePenalties = round.areTherePenalties
                            GamePageListItemState(
                                roundNum = round.roundNumber,
                                handPoints = round.handPoints,
                                isBestHand = round.isBestHand,
                                isEastWinner = round.winnerInitialSeat == TableWinds.EAST,
                                isSouthWinner = round.winnerInitialSeat == TableWinds.SOUTH,
                                isWestWinner = round.winnerInitialSeat == TableWinds.WEST,
                                isNorthWinner = round.winnerInitialSeat == TableWinds.NORTH,
                                isEastLooser = round.discarderInitialSeat == TableWinds.EAST,
                                isSouthLooser = round.discarderInitialSeat == TableWinds.SOUTH,
                                isWestLooser = round.discarderInitialSeat == TableWinds.WEST,
                                isNorthLooser = round.discarderInitialSeat == TableWinds.NORTH,
                                roundPointsEastSeat = round.pointsP1,
                                roundPointsSouthSeat = round.pointsP2,
                                roundPointsWestSeat = round.pointsP3,
                                roundPointsNorthSeat = round.pointsP4,
                                penaltiesEastSeat = round.penaltyP1.takeIf { areTherePenalties },
                                penaltiesSouthSeat = round.penaltyP2.takeIf { areTherePenalties },
                                penaltiesWestSeat = round.penaltyP3.takeIf { areTherePenalties },
                                penaltiesNorthSeat = round.penaltyP4.takeIf { areTherePenalties },
                                roundTotalPointsEastSeat = round.totalPointsP1,
                                roundTotalPointsSouthSeat = round.totalPointsP2,
                                roundTotalPointsWestSeat = round.totalPointsP3,
                                roundTotalPointsNorthSeat = round.totalPointsP4,
                            )
                        },
                        gamePageListPenaltiesFooterState = GamePageListFooterState(
                            title = Res.string.penalties,
                            pointsPlayerEastSeat = game.ongoingOrLastRound.penaltyP1,
                            pointsPlayerSouthSeat = game.ongoingOrLastRound.penaltyP2,
                            pointsPlayerWestSeat = game.ongoingOrLastRound.penaltyP3,
                            pointsPlayerNorthSeat = game.ongoingOrLastRound.penaltyP4,
                        ).takeIf { game.ongoingOrLastRound.areTherePenalties },
                        gamePageListTotalsFooterState = GamePageListFooterState(
                            title = Res.string.totals,
                            pointsPlayerEastSeat = gameTotalPoints.first(),
                            pointsPlayerSouthSeat = gameTotalPoints.second(),
                            pointsPlayerWestSeat = gameTotalPoints.third(),
                            pointsPlayerNorthSeat = gameTotalPoints.fourth(),
                        ),
                    )
                )
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, GameScreenState())
}
