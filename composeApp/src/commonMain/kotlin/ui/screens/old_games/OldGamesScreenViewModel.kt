package ui.screens.old_games

import domain.model.UiGame
import domain.use_cases.GetAllGamesFlowUseCase
import domain.use_cases.utils.fourth
import domain.use_cases.utils.second
import domain.use_cases.utils.third
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ui.common.BaseViewModel
import ui.common.components.SeatState
import ui.common.components.SmallSeatsState

class OldGamesScreenViewModel(
    getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
) : BaseViewModel() {

    val screenStateFlow: StateFlow<OldGamesScreenState> = getAllGamesFlowUseCase.invoke()
        .map(::toOldGamesScreenState)
        .stateIn(viewModelScope, SharingStarted.Lazily, OldGamesScreenState())

    private fun toOldGamesScreenState(games: List<UiGame>) = OldGamesScreenState(
        gamesStates = games.map { game ->
            val winds = game.getSeatsCurrentWind()
            val names = game.getPlayersNamesByCurrentSeat()
            val points = game.getPlayersTotalPointsByCurrentSeat()
            OldGameItemState(
                gameId = game.gameId,
                oldGameItemHeaderState = OldGameItemHeaderState(
                    gameName = game.gameName.ifEmpty { "#${game.gameId}" }
                ),
                oldGameItemBodyState = OldGameItemBodyState(
                    smallSeatsState = SmallSeatsState(
                        eastSeat = SeatState(
                            wind = winds.first(),
                            name = names.first(),
                            points = points.first(),
                        ),
                        southSeat = SeatState(
                            wind = winds.second(),
                            name = names.second(),
                            points = points.second(),
                        ),
                        westSeat = SeatState(
                            wind = winds.third(),
                            name = names.third(),
                            points = points.third(),
                        ),
                        northSeat = SeatState(
                            wind = winds.fourth(),
                            name = names.fourth(),
                            points = points.fourth(),
                        )
                    ),
                    oldGameItemBodyInfoState = game.getBestHand().let { bestHand ->
                        OldGameItemBodyInfoState(
                            date = game.startDate,
                            numRounds = game.uiRounds.size,
                            bestHandPoints = bestHand.handValue,
                            bestHandPlayerName = bestHand.playerName,
                        )
                    }
                )
            )
        }
    )
}