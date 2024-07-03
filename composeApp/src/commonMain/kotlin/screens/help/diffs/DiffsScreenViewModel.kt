package screens.help.diffs

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import screens.common.ui.BaseViewModel
import screens.help.diffs.repository.DiffsRepository

class DiffsScreenViewModel(
    private val diffsRepository: DiffsRepository,
) : BaseViewModel() {

    val screenStateFlow: StateFlow<DiffsScreenState> = diffsRepository.getDiffsFlow
        .map { DiffsScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, DiffsScreenState())

    fun loadNextInterval() {
        viewModelScope.launch {
            diffsRepository.loadNextInterval()
        }
    }
}