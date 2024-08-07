package screens.game

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
import screens.common.model.enums.TableWinds
import screens.common.ui.BaseViewModel
import screens.common.ui.GameId
import screens.common.ui.NOT_SET_GAME_ID
import screens.common.ui.SmallSeatState
import screens.common.ui.SmallSeatsState
import screens.common.use_cases.GetOneGameFlowUseCase
import screens.common.use_cases.utils.fourth
import screens.common.use_cases.utils.second
import screens.common.use_cases.utils.third

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
                val gameWinds = game.getSeatsCurrentWind()
                val gameNames = game.getPlayersNamesByCurrentSeat()
                val gameTotalPoints = game.getPlayersTotalPointsByCurrentSeat()
                val ongoingRound = game.ongoingOrLastRound
                GameScreenState(
                    gamePageTableState = GamePageTableState(
                        gameName = game.gameName,
                        smallSeatsState = SmallSeatsState(
                            eastSeat = SmallSeatState(
                                wind = gameWinds.first(),
                                name = gameNames.first(),
                                points = gameTotalPoints.first(),
                            ),
                            southSeat = SmallSeatState(
                                wind = gameWinds.second(),
                                name = gameNames.second(),
                                points = gameTotalPoints.second(),
                            ),
                            westSeat = SmallSeatState(
                                wind = gameWinds.third(),
                                name = gameNames.third(),
                                points = gameTotalPoints.third(),
                            ),
                            northSeat = SmallSeatState(
                                wind = gameWinds.fourth(),
                                name = gameNames.fourth(),
                                points = gameTotalPoints.fourth(),
                            )
                        ),
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
                            pointsPlayerEastSeat = ongoingRound.penaltyP1,
                            pointsPlayerSouthSeat = ongoingRound.penaltyP2,
                            pointsPlayerWestSeat = ongoingRound.penaltyP3,
                            pointsPlayerNorthSeat = ongoingRound.penaltyP4,
                        ).takeIf { ongoingRound.areTherePenalties },
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
