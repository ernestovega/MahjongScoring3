package dialogs.hand_actions

import LocalNavController
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dialogs.DiffsState
import dialogs.confirmation.ConfirmationDialog
import dialogs.error.ErrorDialog
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.cancel_penalties
import mahjongscoring3.composeapp.generated.resources.cancel_playername_penalties
import mahjongscoring3.composeapp.generated.resources.draw
import mahjongscoring3.composeapp.generated.resources.hu
import mahjongscoring3.composeapp.generated.resources.penalty
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.common.model.states.ScreenState
import screens.common.ui.PlayerDiffsBig
import screens.common.ui.SeatState
import screens.common.use_cases.utils.fromJson

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
    onDismissRequest: () -> Unit,
    goToHuDialog: (selectedSeat: SeatState) -> Unit,
    goToPenaltyDialog: (selectedSeat: SeatState) -> Unit,
    viewModel: HandActionsDialogViewModel = koinViewModel<HandActionsDialogViewModel>(),
) {
    val navController = LocalNavController.current
    val state by viewModel.screenStateFlow.collectAsState()

    LaunchedEffect(navController) {
        navController
            .currentBackStackEntry
            ?.arguments
            ?.getString("selectedSeat")
            ?.fromJson<SeatState>()
            ?.let(viewModel::setSelectedSeatState)
    }

    HandActionsDialogInternal(
        state = state,
        onDismissRequest = onDismissRequest,
        goToHuDialog = goToHuDialog,
        goToPenaltyDialog = goToPenaltyDialog,
        onDrawClick = { viewModel.saveDrawRound() },
        onCancelPenaltiesClick = { viewModel.cancelPenalties(state.data.selectedSeatState) },
    )
}

@Composable
fun HandActionsDialogInternal(
    state: ScreenState<HandActionsDialogState>,
    onDismissRequest: () -> Unit,
    goToHuDialog: (selectedSeat: SeatState) -> Unit,
    goToPenaltyDialog: (selectedSeat: SeatState) -> Unit,
    onDrawClick: () -> Unit,
    onCancelPenaltiesClick: () -> Unit,
) {

    var isCancelPenaltiesConfirmationDialogVisible by remember { mutableStateOf(false) }

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
                HandActionButton(
                    text = stringResource(Res.string.hu),
                    onClick = {
                        onDismissRequest()
                        goToHuDialog(state.data.selectedSeatState)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Draw
                HandActionButton(
                    text = stringResource(Res.string.draw),
                    onClick = {
                        onDrawClick()
                        onDismissRequest()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Penalty
                HandActionButton(
                    text = stringResource(Res.string.penalty),
                    onClick = {
                        onDismissRequest()
                        goToPenaltyDialog(state.data.selectedSeatState)
                    },
                )

                // Cancel penalties
                if (state.data.isCancelPenaltiesVisible) {

                    Spacer(modifier = Modifier.height(8.dp))

                    HandActionButton(
                        text = stringResource(Res.string.cancel_penalties),
                        onClick = { isCancelPenaltiesConfirmationDialogVisible = true },
                    )

                    if (isCancelPenaltiesConfirmationDialogVisible) {
                        ConfirmationDialog(
                            title = stringResource(Res.string.cancel_playername_penalties)
                                .replace("{player_name}", state.data.selectedSeatState.name),
                            onDismissRequest = {
                                isCancelPenaltiesConfirmationDialogVisible = false
                            },
                            onConfirmClick = {
                                onCancelPenaltiesClick()
                                onDismissRequest()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Text(text.uppercase())
    }
}
