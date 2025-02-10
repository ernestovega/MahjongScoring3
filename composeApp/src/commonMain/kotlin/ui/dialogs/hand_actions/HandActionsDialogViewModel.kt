package ui.dialogs.hand_actions

import domain.model.enums.TableWinds
import domain.model.states.ScreenState
import domain.use_cases.CancelAllPenaltiesUseCase
import domain.use_cases.HuDrawUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.common.BaseViewModel
import ui.common.components.SeatState
import ui.screens.help.diffs.Diff

class HandActionsDialogViewModel(
    private val huDrawUseCase: HuDrawUseCase,
    private val cancelAllPenaltiesUseCase: CancelAllPenaltiesUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow<ScreenState<HandActionsDialogState>>(
        ScreenState.Loading(
            HandActionsDialogState(
            selectedSeatState = SeatState(
                wind = TableWinds.NONE,
                name = "...",
                points = 0,
            ),
            areDiffsVisible = false,
            diffsState = DiffsState(playerDiffs = Diff(pointsNeeded = 0)),
            isCancelPenaltiesVisible = false,
        )
        )
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

    fun cancelPenalties(selectedSeatState: SeatState) {
//        cancelAllPenaltiesUseCase.invoke(selectedSeatState)
    }
}
