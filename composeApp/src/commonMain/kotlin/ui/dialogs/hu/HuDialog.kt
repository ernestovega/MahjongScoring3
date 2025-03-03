package ui.dialogs.hu

import LocalNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import domain.model.enums.TableWinds
import domain.use_cases.utils.fromJson
import kotlinx.coroutines.launch
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.cancel
import mahjongscoring3.composeapp.generated.resources.confirm
import mahjongscoring3.composeapp.generated.resources.discarder
import mahjongscoring3.composeapp.generated.resources.points
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ui.common.components.DialogButton
import ui.common.components.GameId
import ui.common.components.NumPad
import ui.common.components.SeatState
import ui.common.components.SmallSeat

data class HuDialogState(
    val selectedSeat: SeatState,
    val notSelectedSeats: List<SeatState>,
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HuDialog(
    onDismissRequest: () -> Unit,
    onError: (message: String, Throwable) -> Unit,
    viewModel: HuDialogViewModel = koinViewModel<HuDialogViewModel>(),
) {
    val navController = LocalNavController.current
    val state by viewModel.screenStateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(navController) {
        val navArgs = navController.currentBackStackEntry?.arguments
        val gameIdArg = navArgs?.getString("gameId")?.fromJson<GameId>()
        val selectedSeatArg = navArgs?.getString("selectedSeat")?.fromJson<TableWinds>()
        gameIdArg?.let(viewModel::setGameId)
        selectedSeatArg?.let(viewModel::setSelectedSeatWind)
    }

    HuDialogInternal(
        state = state.data,
        onDismissRequest = onDismissRequest,
        onConfirmClick = { points, discarderSeat ->
            coroutineScope.launch {
                if (discarderSeat == TableWinds.NONE) {
                    viewModel.setHuSelfPick(points)
                } else {
                    viewModel.setHuDiscard(discarderSeat, points)
                }.fold({ onDismissRequest() }, { onError("Failed to set hu", it) })
            }
        },
    )
}

@Composable
private fun HuDialogInternal(
    state: HuDialogState,
    onDismissRequest: () -> Unit,
    onConfirmClick: (points: Int, discarderSeat: TableWinds) -> Unit,
) {
    var huPoints by remember { mutableStateOf(0) }
    var discarderSeat by remember { mutableStateOf(TableWinds.NONE) }

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
                    text = state.selectedSeat.name,
                    fontSize = 24.sp,
                    maxLines = 1,
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(16.dp),
                )

                // Discarder
                Text(
                    textAlign = TextAlign.Center,
                    text = "${stringResource(Res.string.discarder)}:",
                    fontSize = 20.sp,
                )

                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    state.notSelectedSeats.forEach { seatState ->
                        DiscarderButton(
                            seatState = seatState,
                            isSelected = seatState.wind == discarderSeat,
                            onClick = {
                                discarderSeat = if (seatState.wind == discarderSeat) TableWinds.NONE else seatState.wind
                            },
                        )
                    }
                }

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // NumPad
                NumPad(
                    title = stringResource(Res.string.points),
                    onNumberChanged = { huPoints = it },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row {
                    Spacer(modifier = Modifier.weight(1f))

                    DialogButton(
                        text = stringResource(Res.string.cancel),
                        onClick = onDismissRequest,
                    )

                    DialogButton(
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                        text = stringResource(Res.string.confirm),
                        enabled = huPoints >= 8,
                        onClick = { onConfirmClick(huPoints, discarderSeat) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.DiscarderButton(
    seatState: SeatState,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .weight(1f)
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = if (isSelected) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface,
        ),
        border = if (isSelected) BorderStroke(4.dp, MaterialTheme.colors.secondary) else null,
        onClick = onClick,
    ) {
        SmallSeat(seatState)
    }
}
