package dialogs

import kotlinx.coroutines.launch
import screens.common.ui.BaseViewModel
import screens.common.use_cases.CreateGameUseCase

class CreateGameDialogViewModel(
    private val createGameUseCase: CreateGameUseCase,
) : BaseViewModel() {

    internal fun createGame(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ) {
        viewModelScope.launch {
            createGameUseCase.invoke(
                gameName = "MMC '25",
                nameP1 = "Shari Warner",
                nameP2 = "Carter French",
                nameP3 = "Burt Lynch",
                nameP4 = "Jaime Douglas"
            )
        }
    }
}