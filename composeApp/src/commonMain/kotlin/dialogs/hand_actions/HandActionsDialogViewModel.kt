package dialogs.hand_actions

import dialogs.DiffsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import screens.common.model.enums.TableWinds
import screens.common.model.states.ScreenState
import screens.common.ui.BaseViewModel
import screens.common.ui.SeatState
import screens.common.use_cases.HuDrawUseCase
import screens.help.diffs.Diff

class HandActionsDialogViewModel(
    private val huDrawUseCase: HuDrawUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow<ScreenState<HandActionsDialogState>>(
        ScreenState.Loading(HandActionsDialogState(
            selectedSeatState = SeatState(
                wind = TableWinds.NONE,
                name = "...",
                points = 0,
            ),
            areDiffsVisible = false,
            diffsState = DiffsState(playerDiffs = Diff(pointsNeeded = 0)),
            isCancelPenaltiesVisible = false,
        ))
    )

    fun setSelectedSeatState(selectedSeatState: SeatState) {
        viewModelScope.launch {
            _screenStateFlow.emit(
                ScreenState.Success(
                    HandActionsDialogState(
                        selectedSeatState = selectedSeatState,
                        areDiffsVisible = true,
                        diffsState = DiffsState(Diff(64)),
                        isCancelPenaltiesVisible = true,
                    )
                )
            )
        }
    }

    val screenStateFlow: StateFlow<ScreenState<HandActionsDialogState>> =
        _screenStateFlow.asStateFlow()

    fun saveDrawRound() {
//        huDrawUseCase.invoke(uiGame)
    }
}
