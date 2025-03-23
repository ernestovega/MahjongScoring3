package com.etologic.mahjongscoring.screens.help.fan

import data.repositories.fan.FanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.etologic.mahjongscoring.common.BaseViewModel

class FanScreenViewModel(
    fanRepository: FanRepository,
) : com.etologic.mahjongscoring.common.BaseViewModel() {

    val screenStateFlow: StateFlow<FanScreenState> = fanRepository.getFanFlow
        .map { FanScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, FanScreenState())
}