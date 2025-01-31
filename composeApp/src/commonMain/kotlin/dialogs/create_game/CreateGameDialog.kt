package dialogs.create_game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dialogs.error.ErrorDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.cancel
import mahjongscoring3.composeapp.generated.resources.confirm
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.east_player
import mahjongscoring3.composeapp.generated.resources.game_name
import mahjongscoring3.composeapp.generated.resources.north_player
import mahjongscoring3.composeapp.generated.resources.south_player
import mahjongscoring3.composeapp.generated.resources.west_player
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.common.model.states.ScreenState
import screens.common.model.states.error
import screens.common.ui.GameId
import screens.common.ui.NameTextField
import screens.common.ui.NameTextFieldState

@Immutable
data class CreateGameDialogState(
    val gameName: String = "",
    val nameP1: String = "",
    val nameP2: String = "",
    val nameP3: String = "",
    val nameP4: String = "",
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateGameDialog(
    onDismissRequest: () -> Unit,
    navigateToGame: (gameId: GameId) -> Unit,
    viewModel: CreateGameDialogViewModel = koinViewModel<CreateGameDialogViewModel>(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val state by viewModel.screenStateFlow.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (state is ScreenState.Error) {
        ErrorDialog(state.error)
    } else {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Title
                    Text(
                        text = stringResource(Res.string.create_game),
                        style = MaterialTheme.typography.h6
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // TextFields
                    NameTextField(
                        state = NameTextFieldState(state.data.gameName, Res.string.game_name),
                        focusRequester = focusRequester,
                        onNameChanged = { viewModel.updateNames(gameName = it.trim()) },
                    )
                    NameTextField(
                        state = NameTextFieldState(state.data.nameP1, Res.string.east_player),
                        onNameChanged = { viewModel.updateNames(nameP1 = it.trim()) },
                    )
                    NameTextField(
                        state = NameTextFieldState(state.data.nameP2, Res.string.south_player),
                        onNameChanged = { viewModel.updateNames(nameP2 = it.trim()) },
                    )
                    NameTextField(
                        state = NameTextFieldState(state.data.nameP3, Res.string.west_player),
                        onNameChanged = { viewModel.updateNames(nameP3 = it.trim()) },
                    )
                    NameTextField(
                        state = NameTextFieldState(state.data.nameP4, Res.string.north_player),
                        onNameChanged = { viewModel.updateNames(nameP4 = it.trim()) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(stringResource(Res.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = {
                            coroutineScope.launch {
                                viewModel.createGame()?.let {
                                    navigateToGame(it)
                                }
                            }
                        }) {
                            Text(stringResource(Res.string.confirm))
                        }
                    }
                }
            }
        }
    }
}
