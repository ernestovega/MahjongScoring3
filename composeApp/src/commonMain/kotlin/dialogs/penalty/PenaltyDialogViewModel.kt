package dialogs.penalty

import dialogs.hand_actions.HandActionsDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import screens.common.model.enums.TableWinds
import screens.common.model.states.ScreenState
import screens.common.ui.BaseViewModel
import screens.common.ui.SeatState
import screens.common.use_cases.SetPenaltyUseCase

class PenaltyDialogViewModel(
    private val setPenaltyUseCase: SetPenaltyUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow<ScreenState<SeatState>>(
        ScreenState.Loading(
            SeatState(
                wind = TableWinds.NONE,
                name = "...",
                points = 0,
            ),
        )
    )

    fun setSelectedSeatState(selectedSeat: SeatState) {
        viewModelScope.launch {
            _screenStateFlow.emit(ScreenState.Success(selectedSeat))
        }
    }

    val screenStateFlow: StateFlow<ScreenState<SeatState>> =
        _screenStateFlow.asStateFlow()

    fun setPenalty(
        selectedSeat: SeatState,
        penalty: Int,
        isDivided: Boolean,
    ) {
//        setPenaltyUseCase.invoke(selectedSeatState)
    }
}
