package ui.dialogs.hu

import domain.model.enums.TableWinds
import domain.model.getCurrentSeatStates
import domain.model.states.ScreenState
import domain.use_cases.GetOneGameFlowUseCase
import domain.use_cases.HuDiscardUseCase
import domain.use_cases.HuSelfPickUseCase
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
import ui.common.with

@OptIn(ExperimentalCoroutinesApi::class)
class HuDialogViewModel(
    private val oneGameFlowUseCase: GetOneGameFlowUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
) : BaseViewModel() {

    private val _gameId = MutableStateFlow(NOT_SET_GAME_ID)
    private val _selectedSeatWind = MutableStateFlow(TableWinds.NONE)

    private val gameFlow = _gameId.flatMapMerge { gameId -> oneGameFlowUseCase.invoke(gameId) }
        .stateIn(viewModelScope, WhileSubscribed(), null)

    val screenStateFlow: StateFlow<ScreenState<HuDialogState>> =
        combine(
            _selectedSeatWind,
            gameFlow.filterNotNull(),
        ) { selectedSeatWind, game ->
            val currentSeats = game.getCurrentSeatStates()
            val selectedSeat = currentSeats[selectedSeatWind]
            val notSelectedSeats = currentSeats.asList().minus(selectedSeat)
            ScreenState.Success(HuDialogState(selectedSeat, notSelectedSeats))
        }.stateIn(
            viewModelScope, WhileSubscribed(), ScreenState.Loading(
                HuDialogState(
                    SeatState(TableWinds.NONE),
                    listOf(
                        SeatState(TableWinds.NONE),
                        SeatState(TableWinds.NONE),
                        SeatState(TableWinds.NONE)
                    )
                )
            )
        )

    fun setGameId(gameId: GameId) {
        _gameId.value = gameId
    }

    fun setSelectedSeatWind(selectedSeatWind: TableWinds) {
        _selectedSeatWind.value = selectedSeatWind
    }

    suspend fun setHuDiscard(
        discarderSeat: TableWinds,
        points: Int,
    ): Result<Boolean> =
        gameFlow.with { game ->
            val uiRound = game.ongoingOrLastRound
            val winnerInitialSeat =
                game.getPlayerInitialSeatByOngoingOrLastRoundSeat(_selectedSeatWind.value)
            val discarderInitialSeat =
                game.getPlayerInitialSeatByOngoingOrLastRoundSeat(discarderSeat)
            huDiscardUseCase.invoke(uiRound, winnerInitialSeat, discarderInitialSeat, points)
        }

    suspend fun setHuSelfPick(points: Int): Result<Boolean> =
        gameFlow.with { game ->
            val uiRound = game.ongoingOrLastRound
            val winnerInitialSeat = game.getPlayerInitialSeatByOngoingOrLastRoundSeat(_selectedSeatWind.value)
            huSelfPickUseCase.invoke(uiRound, winnerInitialSeat, points)
        }
}
