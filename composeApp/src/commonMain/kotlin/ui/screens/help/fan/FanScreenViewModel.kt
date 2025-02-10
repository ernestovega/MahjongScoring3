package ui.screens.help.fan

import data.repositories.fan.FanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ui.common.BaseViewModel

class FanScreenViewModel(
    fanRepository: FanRepository,
) : BaseViewModel() {

    val screenStateFlow: StateFlow<FanScreenState> = fanRepository.getFanFlow
        .map { FanScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, FanScreenState())
}