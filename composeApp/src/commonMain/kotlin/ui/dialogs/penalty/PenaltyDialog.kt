package ui.dialogs.penalty

import LocalNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
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
import mahjongscoring3.composeapp.generated.resources.divided
import mahjongscoring3.composeapp.generated.resources.penalty
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ui.common.components.DialogButton
import ui.common.components.GameId
import ui.common.components.NumPad
import ui.common.components.SeatState

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PenaltyDialog(
    onDismissRequest: () -> Unit,
    onError: (message: String, Throwable) -> Unit,
    viewModel: PenaltyDialogViewModel = koinViewModel<PenaltyDialogViewModel>(),
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

    PenaltyDialogInternal(
        selectedSeat = state.data,
        onDismissRequest = onDismissRequest,
        onConfirmClick = { points, isDivided ->
            coroutineScope.launch {
                viewModel.setPenalty(points, isDivided)
                    .fold(
                        onSuccess = { onDismissRequest() },
                        onFailure = { onError("Failed to set penalty", it) },
                    )
            }
        },
    )
}

@Composable
fun PenaltyDialogInternal(
    selectedSeat: SeatState,
    onDismissRequest: () -> Unit,
    onConfirmClick: (points: Int, isDivided: Boolean) -> Unit,
) {
    var penaltyPoints by remember { mutableStateOf(0) }
    var isDivided by remember { mutableStateOf(true) }

    Dialog(onDismissRequest) {
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
                    text = selectedSeat.name,
                    fontSize = 24.sp,
                    maxLines = 1,
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(16.dp),
                )

                // NumPad
                NumPad(
                    title = stringResource(Res.string.penalty),
                    onNumberChanged = { penaltyPoints = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDivided,
                        onCheckedChange = { isChecked ->
                            if (isDivided != isChecked) isDivided = isChecked
                        },
                    )
                    Text(
                        text = stringResource(Res.string.divided),
                        fontSize = 20.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                        enabled = penaltyPoints > 0 && (!isDivided || penaltyPoints % 3 == 0),
                        onClick = { onConfirmClick(penaltyPoints, isDivided) }
                    )
                }
            }
        }
    }
}
