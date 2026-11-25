package com.etologic.mahjongscoring3.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LanguageState {
    data object Loading : LanguageState
    /** [language] null means "use the system language". */
    data class Loaded(val language: String?) : LanguageState
}

class MainViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _activeGameId = MutableStateFlow(NOT_SET_GAME_ID)
    val activeGameId: StateFlow<GameId> = _activeGameId.asStateFlow()

    val languageState: StateFlow<LanguageState> =
        settingsRepository.languageFlow
            .map { LanguageState.Loaded(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, LanguageState.Loading)

    fun updateActiveGameId(gameId: GameId) {
        _activeGameId.value = gameId
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch { settingsRepository.saveLanguage(language) }
    }
}
