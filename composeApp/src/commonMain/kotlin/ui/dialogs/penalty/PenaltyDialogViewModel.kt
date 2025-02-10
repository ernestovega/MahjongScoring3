package ui.dialogs.penalty

import domain.model.enums.TableWinds
import domain.model.states.ScreenState
import domain.use_cases.SetPenaltyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.common.BaseViewModel
import ui.common.components.SeatState

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
