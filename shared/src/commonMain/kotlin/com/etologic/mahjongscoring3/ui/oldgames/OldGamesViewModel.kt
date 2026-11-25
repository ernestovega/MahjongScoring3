package com.etologic.mahjongscoring3.ui.oldgames

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.ShareGameOptions
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.usecases.CreateGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.DeleteGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToTextUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.domain.usecases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring3.ui.common.BaseViewModel
import com.etologic.mahjongscoring3.ui.common.GameNames
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface OldGamesUiState {
    data object Loading : OldGamesUiState
    data class Loaded(val oldGamesList: List<UiGame>) : OldGamesUiState
}

class OldGamesViewModel(
    getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
    private val createGameUseCase: CreateGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportGameToJsonUseCase: ExportGameToJsonUseCase,
    private val exportGamesToJsonUseCase: ExportGamesToJsonUseCase,
    private val importGamesFromJsonUseCase: ImportGamesFromJsonUseCase,
) : BaseViewModel() {

    val uiState: StateFlow<OldGamesUiState> =
        getAllGamesFlowUseCase()
            .map { OldGamesUiState.Loaded(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OldGamesUiState.Loading)

    fun createGame(names: GameNames, onSuccess: (GameId) -> Unit) {
        viewModelScope.launch {
            createGameUseCase(names.gameName, names.nameP1, names.nameP2, names.nameP3, names.nameP4)
                .fold(onSuccess, ::showError)
        }
    }

    fun deleteGame(gameId: GameId) {
        viewModelScope.launch {
            deleteGameUseCase(gameId).onFailure(::showError)
        }
    }

    fun shareGame(
        gameId: GameId,
        option: ShareGameOptions,
        showShareText: (String) -> Unit,
        showShareFile: (ExportedFile) -> Unit,
    ) {
        viewModelScope.launch {
            when (option) {
                ShareGameOptions.TEXT -> exportGameToTextUseCase(gameId).fold(showShareText, ::showError)
                ShareGameOptions.CSV -> exportGameToCsvUseCase(gameId).fold(showShareFile, ::showError)
                ShareGameOptions.JSON -> exportGameToJsonUseCase(gameId).fold(showShareFile, ::showError)
            }
        }
    }

    fun exportGames(showShareFile: (ExportedFile) -> Unit) {
        viewModelScope.launch {
            exportGamesToJsonUseCase().fold(showShareFile, ::showError)
        }
    }

    fun importGames(jsonText: String) {
        viewModelScope.launch {
            importGamesFromJsonUseCase(jsonText).onFailure(::showError)
        }
    }
}
