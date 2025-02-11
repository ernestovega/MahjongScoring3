package ui.dialogs.penalty

import domain.model.enums.TableWinds
import domain.model.getCurrentSeatStates
import domain.model.states.ScreenState
import domain.use_cases.GetOneGameFlowUseCase
import domain.use_cases.PenaltyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn
import ui.common.BaseViewModel
import ui.common.components.GameId
import ui.common.components.NOT_SET_GAME_ID
import ui.common.components.SeatState

@OptIn(ExperimentalCoroutinesApi::class)
class PenaltyDialogViewModel(
    private val oneGameFlowUseCase: GetOneGameFlowUseCase,
    private val penaltyUseCase: PenaltyUseCase,
) : BaseViewModel() {

    private val _gameId = MutableStateFlow(NOT_SET_GAME_ID)
    private val _selectedSeatWind = MutableStateFlow(TableWinds.NONE)

    private val gameFlow = _gameId.flatMapMerge { gameId -> oneGameFlowUseCase.invoke(gameId) }
        .stateIn(viewModelScope, WhileSubscribed(), null)

    val screenStateFlow: StateFlow<ScreenState<SeatState>> =
        combine(
            _selectedSeatWind,
            gameFlow.filterNotNull(),
        ) { selectedSeatWind, game ->
            val currentSeats = game.getCurrentSeatStates()
            val selectedSeat = currentSeats[selectedSeatWind]
            ScreenState.Success(selectedSeat)
        }.stateIn(
            viewModelScope, WhileSubscribed(), ScreenState.Loading(
                SeatState(
                    wind = TableWinds.NONE,
                    name = "...",
                    points = 0,
                )
            )
        )

    fun setGameId(gameId: GameId) {
        _gameId.value = gameId
    }

    fun setSelectedSeatWind(selectedSeatWind: TableWinds) {
        _selectedSeatWind.value = selectedSeatWind
    }

    suspend fun setPenalty(points: Int, isDivided: Boolean): Result<Boolean> =
        gameFlow.value?.let { game ->
            val penalizedPlayerInitialSeat =
                game.getPlayerInitialSeatByOngoingOrLastRoundSeat(_selectedSeatWind.value)
            penaltyUseCase.invoke(
                roundId = game.ongoingOrLastRound.roundId,
                points = points,
                isDivided = isDivided,
                penalizedPlayerInitialSeat = penalizedPlayerInitialSeat,
            )
        } ?: Result.failure(Exception("Game not found"))
}
