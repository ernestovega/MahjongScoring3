package screens.old_games

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import screens.common.model.TableWinds
import screens.common.ui.BaseViewModel
import screens.common.ui.SmallSeatState
import screens.common.ui.SmallSeatsState
import screens.common.use_cases.GetAllGamesFlowUseCase

class OldGamesScreenViewModel(
    getAllGamesFlowUseCase: GetAllGamesFlowUseCase
) : BaseViewModel() {

    val screenStateFlow: StateFlow<OldGamesScreenState> = getAllGamesFlowUseCase.invoke()
        .map { games ->
            OldGamesScreenState(
                gamesStates = games.map { game ->
                    OldGameItemState(
                        oldGameItemHeaderState = OldGameItemHeaderState(gameName = ""),
                        oldGameItemBodyState = OldGameItemBodyState(
                            smallSeatsState = SmallSeatsState(
                                eastSeat = SmallSeatState(
                                    wind = TableWinds.SOUTH,
                                    name = "",
                                    points = 0
                                ), 
                                southSeat = SmallSeatState(
                                    wind = TableWinds.SOUTH,
                                    name = "",
                                    points = 0
                                ), 
                                westSeat = SmallSeatState(
                                    wind = TableWinds.SOUTH,
                                    name = "",
                                    points = 0
                                ), 
                                northSeat = SmallSeatState(
                                    wind = TableWinds.SOUTH,
                                    name = "",
                                    points = 0
                                )
                            ), 
                            oldGameItemBodyGameDataState = OldGameItemBodyGameDataState(
                                date = Clock.System.now(),
                                numRounds = 0,
                                bestHandPlayerName = "",
                                bestHandPoints = 0
                            )
                        )
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, OldGamesScreenState())
}