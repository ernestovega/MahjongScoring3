package ui.dialogs.confirmation

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.are_you_sure
import mahjongscoring3.composeapp.generated.resources.no
import mahjongscoring3.composeapp.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmationDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(stringResource(Res.string.are_you_sure)) },
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text(stringResource(Res.string.yes).uppercase())
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(Res.string.no).uppercase())
            }
        }
    )
}
