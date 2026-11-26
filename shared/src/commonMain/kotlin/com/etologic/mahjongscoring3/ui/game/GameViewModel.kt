package com.etologic.mahjongscoring3.ui.game

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.HuData
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.PenaltyData
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.ShareGameOptions
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import com.etologic.mahjongscoring3.domain.usecases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring3.domain.usecases.DeleteRoundUseCase
import com.etologic.mahjongscoring3.domain.usecases.EditGameNamesUseCase
import com.etologic.mahjongscoring3.domain.usecases.EndGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToTextUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDiscardUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDrawUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuSelfPickUseCase
import com.etologic.mahjongscoring3.domain.usecases.ResumeGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.SetPenaltyUseCase
import com.etologic.mahjongscoring3.ui.common.BaseViewModel
import com.etologic.mahjongscoring3.ui.common.GameNames
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GamePages(val index: Int) { TABLE(0), LIST(1) }

/** One-shot instruction for the UI, as MS2 "pageToShow" pair. [id] makes equal events distinct. */
sealed interface GamePageEvent {
    val id: Long

    /** After saving a hand: go to the list and highlight the last round. */
    data class HighlightLastRound(override val id: Long) : GamePageEvent

    /** After the last hand: go to the table, show the ranking arrangement and the final results dialog. */
    data class ShowRanking(override val id: Long) : GamePageEvent
}

sealed interface GameUiState {
    data object Loading : GameUiState
    data class Loaded(
        val game: UiGame,
        val isDiffsCalcsFeatureEnabled: Boolean,
        val seatsArrangement: SeatsArrangement,
    ) : GameUiState
}

class GameViewModel(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
    private val editGameNamesUseCase: EditGameNamesUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
    private val huDrawUseCase: HuDrawUseCase,
    private val setPenaltyUseCase: SetPenaltyUseCase,
    private val cancelAllPenaltiesUseCase: CancelAllPenaltiesUseCase,
    private val deleteRoundUseCase: DeleteRoundUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val endGameUseCase: EndGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportGameToJsonUseCase: ExportGameToJsonUseCase,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {

    private val gameId = MutableStateFlow(NOT_SET_GAME_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val gameFlow: StateFlow<UiGame?> =
        gameId.filter { it != NOT_SET_GAME_ID }
            .flatMapLatest { getOneGameFlowUseCase(it) }
            .catch { showError(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val isDiffsCalcsFeatureEnabled: StateFlow<Boolean> =
        settingsRepository.isDiffsCalcsFeatureEnabledFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val seatsArrangement: StateFlow<SeatsArrangement> =
        settingsRepository.seatsArrangementFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, SeatsArrangement.DOWN)

    val uiState: StateFlow<GameUiState> =
        combine(gameFlow, isDiffsCalcsFeatureEnabled, seatsArrangement) { game, isDiffsEnabled, arrangement ->
            if (game == null || game.gameId != gameId.value) {
                GameUiState.Loading
            } else {
                GameUiState.Loaded(game, isDiffsEnabled, arrangement)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, GameUiState.Loading)

    private val _shouldShowDiffs = MutableStateFlow(false)
    val shouldShowDiffs: StateFlow<Boolean> = _shouldShowDiffs.asStateFlow()

    /** Seat (by current wind) whose actions dialog is open, or NONE. */
    private val _selectedSeat = MutableStateFlow(TableWinds.NONE)
    val selectedSeat: StateFlow<TableWinds> = _selectedSeat.asStateFlow()

    private val _pageEvent = MutableStateFlow<GamePageEvent?>(null)
    val pageEvent: StateFlow<GamePageEvent?> = _pageEvent.asStateFlow()
    private var nextEventId = 0L

    fun setGameId(newGameId: GameId) {
        if (gameId.value != newGameId) {
            gameId.value = newGameId
            _selectedSeat.value = TableWinds.NONE
            _pageEvent.value = null
        }
    }

    fun consumePageEvent(event: GamePageEvent) {
        if (_pageEvent.value == event) _pageEvent.value = null
    }

    // SELECTED SEAT

    fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.value = wind
    }

    fun unselectSeats() {
        _selectedSeat.value = TableWinds.NONE
    }

    // GAME OPERATIONS

    fun resumeGame() {
        gameFlow.value?.let { game ->
            viewModelScope.launch { resumeGameUseCase(game).onFailure(::showError) }
        }
    }

    fun endGame(onEnded: () -> Unit) {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                endGameUseCase(game)
                    .onSuccess { onEnded() }
                    .onFailure(::showError)
            }
        }
    }

    fun saveGameNames(names: GameNames) {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                editGameNamesUseCase(
                    uiGame = game,
                    newGameName = names.gameName,
                    newNameP1 = names.nameP1,
                    newNameP2 = names.nameP2,
                    newNameP3 = names.nameP3,
                    newNameP4 = names.nameP4,
                ).onFailure(::showError)
            }
        }
    }

    // ROUND OPERATIONS

    fun saveHuDiscardRound(huData: HuData) {
        gameFlow.value?.let { game ->
            viewModelScope.launch { huDiscardUseCase(game, huData).fold({ onRoundSaved() }, ::showError) }
        }
    }

    fun saveHuSelfPickRound(huData: HuData) {
        gameFlow.value?.let { game ->
            viewModelScope.launch { huSelfPickUseCase(game, huData).fold({ onRoundSaved() }, ::showError) }
        }
    }

    fun saveDrawRound() {
        gameFlow.value?.let { game ->
            viewModelScope.launch { huDrawUseCase(game).fold({ onRoundSaved() }, ::showError) }
        }
    }

    private suspend fun onRoundSaved() {
        unselectSeats()
        // Read the game again from the database, because the flow may not have emitted the new round yet
        val isGameEnded = runCatching { getOneGameFlowUseCase(gameId.value).first().isEnded }.getOrDefault(false)
        _pageEvent.value = if (isGameEnded) {
            // When the last hand ends the game, MS2 shows the ranking instead of the list
            settingsRepository.saveSeatsArrangement(SeatsArrangement.RANKING)
            GamePageEvent.ShowRanking(++nextEventId)
        } else {
            GamePageEvent.HighlightLastRound(++nextEventId)
        }
    }

    fun savePenalty(penaltyData: PenaltyData) {
        gameFlow.value?.let { game ->
            viewModelScope.launch { setPenaltyUseCase(game.ongoingRound, penaltyData).onFailure(::showError) }
        }
        unselectSeats()
    }

    fun cancelPenalties() {
        gameFlow.value?.let { game ->
            viewModelScope.launch { cancelAllPenaltiesUseCase(game.ongoingRound).onFailure(::showError) }
        }
        unselectSeats()
    }

    fun removeRound(roundId: RoundId) {
        viewModelScope.launch { deleteRoundUseCase(roundId).onFailure(::showError) }
    }

    // VIEW ACTIONS

    fun nextSeatsOrientation() {
        viewModelScope.launch {
            settingsRepository.saveSeatsArrangement(seatsArrangement.value.next())
        }
    }

    fun setDiffsCalcsFeatureEnabled(isEnabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveDiffsCalcsFeatureEnabled(isEnabled) }
        if (!isEnabled) _shouldShowDiffs.value = false
    }

    fun toggleDiffsView(shouldShow: Boolean) {
        if (isDiffsCalcsFeatureEnabled.value) {
            _shouldShowDiffs.value = shouldShow
        }
    }

    // SHARE

    fun shareGame(
        option: ShareGameOptions,
        showShareText: (String) -> Unit,
        showShareFile: (ExportedFile) -> Unit,
    ) {
        val id = gameFlow.value?.gameId ?: return
        viewModelScope.launch {
            when (option) {
                ShareGameOptions.TEXT -> exportGameToTextUseCase(id).fold(showShareText, ::showError)
                ShareGameOptions.CSV -> exportGameToCsvUseCase(id).fold(showShareFile, ::showError)
                ShareGameOptions.JSON -> exportGameToJsonUseCase(id).fold(showShareFile, ::showError)
            }
        }
    }
}
