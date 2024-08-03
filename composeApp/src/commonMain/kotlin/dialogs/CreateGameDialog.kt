package dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateGameDialog(
    onDismissRequest: () -> Unit,
    navigateToGame: () -> Unit,
    viewModel: CreateGameDialogViewModel = koinViewModel<CreateGameDialogViewModel>(),
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Dialog Title", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(8.dp))

                // TextFields with Icons
                val textFieldsData = listOf("Game name", "East player", "South player", "West player", "North player")
                val textFieldsState = remember { mutableStateListOf(*textFieldsData.toTypedArray()) }

                textFieldsData.forEachIndexed { index, hint ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = textFieldsState[index],
                            onValueChange = { textFieldsState[index] = it },
                            placeholder = { Text(hint) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        viewModel.createGame(
                            textFieldsData[0],
                            textFieldsData[1],
                            textFieldsData[2],
                            textFieldsData[3],
                            textFieldsData[4]
                        )
                        navigateToGame()
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}