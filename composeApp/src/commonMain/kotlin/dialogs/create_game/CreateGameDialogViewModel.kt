package dialogs.create_game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import screens.common.model.states.ScreenState
import screens.common.ui.BaseViewModel
import screens.common.ui.GameId
import screens.common.use_cases.CreateGameUseCase

class CreateGameDialogViewModel(
    private val createGameUseCase: CreateGameUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow<ScreenState<CreateGameDialogState>>(
        ScreenState.Success(CreateGameDialogState())
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
                data = CreateGameDialogState(
                    gameName = gameName ?: value.data.gameName,
                    nameP1 = nameP1 ?: value.data.nameP1,
                    nameP2 = nameP2 ?: value.data.nameP2,
                    nameP3 = nameP3 ?: value.data.nameP3,
                    nameP4 = nameP4 ?: value.data.nameP4,
                ),
            )
        }
    }

    internal suspend fun createGame(): GameId? =
        createGameUseCase.invoke(
            gameName = screenStateFlow.value.data.gameName,
            nameP1 = screenStateFlow.value.data.nameP1,
            nameP2 = screenStateFlow.value.data.nameP2,
            nameP3 = screenStateFlow.value.data.nameP3,
            nameP4 = screenStateFlow.value.data.nameP4,
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
