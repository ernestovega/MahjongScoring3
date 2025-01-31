import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import screens.common.ui.BaseViewModel
import screens.common.ui.NOT_SET_GAME_ID
import screens.common.ui.SeatState

class AppViewModel : BaseViewModel() {

    private val _isDarkModeFlow = MutableStateFlow(false)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow

    private val _ongoingGameId = MutableStateFlow(NOT_SET_GAME_ID)
    val ongoingGameId: StateFlow<Long> = _ongoingGameId.asStateFlow()

    private val _selectedSeatState = MutableStateFlow<SeatState?>(null)
    val selectedSeatState: StateFlow<SeatState?> = _selectedSeatState.asStateFlow()

    fun changeColorMode() = _isDarkModeFlow.update { value -> value.not() }

    fun setOngoingGameId(gameId: Long) = _ongoingGameId.update { gameId }

    fun setSelectedSeat(seatState: SeatState) = _selectedSeatState.update { seatState }
}