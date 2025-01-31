package dialogs.hand_actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dialogs.DiffsState
import dialogs.error.ErrorDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.cancel_penalties
import mahjongscoring3.composeapp.generated.resources.draw
import mahjongscoring3.composeapp.generated.resources.hu
import mahjongscoring3.composeapp.generated.resources.penalty
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.common.model.states.ScreenState
import screens.common.model.states.error
import screens.common.ui.PlayerDiffsBig
import screens.common.ui.SeatState

@Immutable
data class HandActionsDialogState(
    val selectedSeatState: SeatState,
    val areDiffsVisible: Boolean,
    val diffsState: DiffsState,
    val isCancelPenaltiesVisible: Boolean,
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HandActionsDialog(
    selectedSeatState: SeatState,
    onDismissRequest: () -> Unit,
    navigateToHuDialog: (selectedSeatState: SeatState) -> Unit,
    navigateToPenaltyDialog: (selectedSeatState: SeatState) -> Unit,
    navigateToCancelPenaltyDialog: (selectedSeatState: SeatState) -> Unit,
    viewModel: HandActionsDialogViewModel = koinViewModel<HandActionsDialogViewModel>(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    viewModel.setSelectedSeatState(selectedSeatState)
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    // Player
                    Image(
                        modifier = Modifier
                            .width(72.dp)
                            .padding(top = 8.dp),
                        imageVector = Icons.Filled.Home,
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(Res.string.west),
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        text = state.data.selectedSeatState.name,
                        fontSize = 24.sp,
                        maxLines = 1,
                    )

                    Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    // Diffs
                    if (state.data.areDiffsVisible) {
                        PlayerDiffsBig(state.data.diffsState.playerDiffs)
                    }

                    Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    // Hu
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        onClick = { navigateToHuDialog(state.data.selectedSeatState) },
                    ) {
                        Text(text = stringResource(Res.string.hu))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Draw
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveDrawRound()
                                onDismissRequest()
                            }
                        },
                    ) {
                        Text(stringResource(Res.string.draw))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Penalty
                    var isCancelPenaltyVisible by remember { mutableStateOf(true) }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isCancelPenaltyVisible = !isCancelPenaltyVisible },//navigateToPenaltyDialog(state.data.selectedSeatState) },
                    ) {
                        Text(stringResource(Res.string.penalty))
                    }

                    // Cancel penalties
                    if (isCancelPenaltyVisible) {//state.data.isCancelPenaltiesVisible) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigateToCancelPenaltyDialog(state.data.selectedSeatState) },
                        ) {
                            Text(stringResource(Res.string.cancel_penalties))
                        }
                    }
                }
            }
        }
    }
}
