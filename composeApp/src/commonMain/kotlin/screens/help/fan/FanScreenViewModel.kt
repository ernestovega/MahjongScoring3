package screens.help.fan

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import screens.common.ui.BaseViewModel
import screens.help.fan.repository.FanRepository

class FanScreenViewModel(
    fanRepository: FanRepository,
) : BaseViewModel() {

    val screenStateFlow: StateFlow<FanScreenState> = fanRepository.getFanFlow
        .map { FanScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, FanScreenState())
}