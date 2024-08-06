package dialogs

import kotlinx.coroutines.launch
import screens.common.ui.BaseViewModel
import screens.common.ui.GameNames
import screens.common.use_cases.CreateGameUseCase

class CreateGameDialogViewModel(
    private val createGameUseCase: CreateGameUseCase,
) : BaseViewModel() {

    internal fun createGame(namesState: GameNames) {
        viewModelScope.launch {
            createGameUseCase.invoke(
                gameName = namesState.gameName,
                nameP1 = namesState.nameP1,
                nameP2 = namesState.nameP2,
                nameP3 = namesState.nameP3,
                nameP4 = namesState.nameP4,
            )
        }
    }
}