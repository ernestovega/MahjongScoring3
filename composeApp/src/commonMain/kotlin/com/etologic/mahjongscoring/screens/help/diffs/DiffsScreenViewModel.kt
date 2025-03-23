package com.etologic.mahjongscoring.screens.help.diffs

import data.repositories.diffs.DiffsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.etologic.mahjongscoring.common.BaseViewModel

class DiffsScreenViewModel(
    private val diffsRepository: DiffsRepository,
) : com.etologic.mahjongscoring.common.BaseViewModel() {

    val screenStateFlow: StateFlow<DiffsScreenState> = diffsRepository.getDiffsFlow
        .map { DiffsScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, DiffsScreenState())

    fun loadNextInterval() {
        viewModelScope.launch {
            diffsRepository.loadNextInterval()
        }
    }
}