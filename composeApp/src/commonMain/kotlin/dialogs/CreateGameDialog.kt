package dialogs

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import screens.common.ui.GameNames
import screens.common.ui.NameTextField
import screens.common.ui.NameTextFieldState

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateGameDialog(
    onDismissRequest: () -> Unit,
    navigateToGame: () -> Unit,
    viewModel: CreateGameDialogViewModel = koinViewModel<CreateGameDialogViewModel>(),
) {
    var gameNames by remember { mutableStateOf(GameNames()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(Res.string.create_game), style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(8.dp))

                NameTextField(
                    state = NameTextFieldState(gameNames.gameName, Res.string.game_name),
                    focusRequester = focusRequester,
                    onNameChanged = { gameNames = gameNames.copy(gameName = it.trim()) },
                )
                NameTextField(
                    state = NameTextFieldState(gameNames.nameP1, Res.string.east_player),
                    onNameChanged = { gameNames = gameNames.copy(nameP1 = it.trim()) },
                )
                NameTextField(
                    state = NameTextFieldState(gameNames.nameP2, Res.string.south_player),
                    onNameChanged = { gameNames = gameNames.copy(nameP2 = it.trim()) },
                )
                NameTextField(
                    state = NameTextFieldState(gameNames.nameP3, Res.string.west_player),
                    onNameChanged = { gameNames = gameNames.copy(nameP3 = it.trim()) },
                )
                NameTextField(
                    state = NameTextFieldState(gameNames.nameP4, Res.string.north_player),
                    onNameChanged = { gameNames = gameNames.copy(nameP4 = it.trim()) },
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
                        viewModel.createGame(gameNames)
                        navigateToGame()
                    }) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
