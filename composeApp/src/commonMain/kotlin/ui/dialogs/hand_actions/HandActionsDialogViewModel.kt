package ui.dialogs.hand_actions

import domain.model.SeatDiffs
import domain.model.enums.TableWinds
import domain.model.getCurrentSeatStates
import domain.model.states.ScreenState
import domain.use_cases.GetOneGameFlowUseCase
import domain.use_cases.HuUseCase
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
class HandActionsDialogViewModel(
    private val oneGameFlowUseCase: GetOneGameFlowUseCase,
    private val huUseCase: HuUseCase,
    private val penaltyUseCase: PenaltyUseCase,
) : BaseViewModel() {

    private val _gameId = MutableStateFlow(NOT_SET_GAME_ID)
    private val _selectedSeatWind = MutableStateFlow(TableWinds.NONE)

    private val gameFlow = _gameId.flatMapMerge { gameId -> oneGameFlowUseCase.invoke(gameId) }
        .stateIn(viewModelScope, WhileSubscribed(), null)

    val screenStateFlow: StateFlow<ScreenState<HandActionsDialogState>> =
        combine(
            _selectedSeatWind,
            gameFlow.filterNotNull(),
        ) { selectedSeatWind, game ->
            val currentSeats = game.getCurrentSeatStates()
            val selectedSeat = currentSeats[selectedSeatWind]
            val diffs = game.getTableDiffs().seatsDiffs[selectedSeatWind.index]
            ScreenState.Success(HandActionsDialogState(
                selectedSeatState = selectedSeat,
                areDiffsVisible = true,
                seatDiffs = diffs,
                isCancelPenaltiesVisible = false,
            ))
        }.stateIn(
            viewModelScope, WhileSubscribed(), ScreenState.Loading(
                HandActionsDialogState(
                    selectedSeatState = SeatState(
                        wind = TableWinds.NONE,
                        name = "...",
                        points = 0,
                    ),
                    areDiffsVisible = false,
                    seatDiffs = SeatDiffs(TableWinds.NONE, 0),
                    isCancelPenaltiesVisible = false,
                )
            )
        )

    fun setGameId(gameId: GameId) {
        _gameId.value = gameId
    }

    fun setSelectedSeatWind(selectedSeatWind: TableWinds) {
        _selectedSeatWind.value = selectedSeatWind
    }

    suspend fun saveDrawRound(): Result<Boolean> =
        gameFlow.value?.let { game ->
            huUseCase.invoke(
                roundId = game.ongoingOrLastRound.roundId,
                winnerInitialSeat = TableWinds.NONE,
                discarderInitialSeat = TableWinds.NONE,
                points = 0,
            )
        } ?: Result.failure(Exception("Game not found"))

    suspend fun cancelPenalties(): Result<Boolean> =
        gameFlow.value?.let { game ->
            penaltyUseCase.invoke(
                roundId = game.ongoingOrLastRound.roundId,
                points = 0,
                isDivided = false,
                penalizedPlayerInitialSeat = TableWinds.NONE,
            )
        } ?: Result.failure(Exception("Game not found"))
}
