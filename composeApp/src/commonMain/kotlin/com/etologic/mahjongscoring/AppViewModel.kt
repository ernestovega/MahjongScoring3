package com.etologic.mahjongscoring

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.etologic.mahjongscoring.common.BaseViewModel
import com.etologic.mahjongscoring.common.components.NOT_SET_GAME_ID

class AppViewModel : com.etologic.mahjongscoring.common.BaseViewModel() {

    private val _isDarkModeFlow = MutableStateFlow(false)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow

    private val _ongoingGameId = MutableStateFlow(NOT_SET_GAME_ID)
    val ongoingGameId: StateFlow<Long> = _ongoingGameId.asStateFlow()

    fun changeColorMode() = _isDarkModeFlow.update { value -> value.not() }

    fun setOngoingGameId(gameId: Long) = _ongoingGameId.update { gameId }

}