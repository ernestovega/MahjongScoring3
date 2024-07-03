package screens.game

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import screens.common.model.TableWinds
import screens.common.ui.BaseViewModel
import screens.common.ui.SmallSeatState
import screens.common.ui.SmallSeatsState
import screens.common.use_cases.GetOneGameFlowUseCase

class GameScreenViewModel(
    getOneGameFlowUseCase: GetOneGameFlowUseCase
) : BaseViewModel() {

    val screenStateFlow: StateFlow<GameScreenState> = getOneGameFlowUseCase.invoke(0)
        .map { game ->
            GameScreenState(
                gamePageTableState = GamePageTableState(
                    gameName = game.gameName,
                    smallSeatsState = SmallSeatsState(
                        eastSeat = SmallSeatState(
                            wind = TableWinds.EAST,
                            name = "",
                            points = 0
                        ), southSeat = SmallSeatState(
                            wind = TableWinds.SOUTH,
                            name = "",
                            points = 0
                        ), westSeat = SmallSeatState(
                            wind = TableWinds.WEST,
                            name = "",
                            points = 0
                        ), northSeat = SmallSeatState(
                            wind = TableWinds.NORTH,
                            name = "",
                            points = 0
                        )
                    )
                ),
                gamePageListState = GamePageListState(
                    gamePageListHeaderState = GamePageListHeaderState(
                        playerNameEastSeat = "",
                        playerNameSouthSeat = "",
                        playerNameWestSeat = "",
                        playerNameNorthSeat = ""
                    ),
                    roundsStates = listOf(
                        GamePageListItemState(),
                        GamePageListItemState(),
                        GamePageListItemState(),
                        GamePageListItemState(),
                    ),
                    gamePageListFooterState = GamePageListFooterState(
                        totalPointsPlayerEastSeat = 0,
                        totalPointsPlayerSouthSeat = 0,
                        totalPointsPlayerWestSeat = 0,
                        totalPointsPlayerNorthSeat = 0,
                        penaltiesPlayerEastSeat = 0,
                        penaltiesPlayerSouthSeat = 0,
                        penaltiesPlayerWestSeat = 0,
                        penaltiesPlayerNorthSeat = 0
                    )
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, GameScreenState())
}