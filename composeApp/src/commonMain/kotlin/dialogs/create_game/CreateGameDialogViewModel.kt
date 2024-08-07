package dialogs.create_game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import screens.common.model.states.ScreenState
import screens.common.ui.BaseViewModel
import screens.common.ui.GameId
import screens.common.ui.GameNames
import screens.common.use_cases.CreateGameUseCase

class CreateGameDialogViewModel(
    private val createGameUseCase: CreateGameUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow<ScreenState<CreateGameDialogState>>(
        ScreenState.Success(CreateGameDialogState(GameNames()))
    )
    val screenStateFlow: StateFlow<ScreenState<CreateGameDialogState>> = _screenStateFlow.asStateFlow()

    internal fun updateNames(
        gameName: String? = null,
        nameP1: String? = null,
        nameP2: String? = null,
        nameP3: String? = null,
        nameP4: String? = null,
    ) {
        _screenStateFlow.update { value ->
            ScreenState.Success(
                CreateGameDialogState(
                    gameNames = GameNames(
                        gameName = gameName ?: value.data.gameNames.gameName,
                        nameP1 = nameP1 ?: value.data.gameNames.nameP1,
                        nameP2 = nameP2 ?: value.data.gameNames.nameP2,
                        nameP3 = nameP3 ?: value.data.gameNames.nameP3,
                        nameP4 = nameP4 ?: value.data.gameNames.nameP4,
                    ),
                    error = null,
                ),
            )
        }
    }

    internal suspend fun createGame(namesState: GameNames): GameId? =
        createGameUseCase.invoke(
            gameName = namesState.gameName,
            nameP1 = namesState.nameP1,
            nameP2 = namesState.nameP2,
            nameP3 = namesState.nameP3,
            nameP4 = namesState.nameP4,
        )
            .onFailure { throwable ->
                _screenStateFlow.update { value ->
                    ScreenState.Error(
                        error = throwable,
                        data = value.data,
                    )
                }
            }
            .getOrNull()
}
