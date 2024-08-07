package dialogs.error

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.close
import mahjongscoring3.composeapp.generated.resources.error
import mahjongscoring3.composeapp.generated.resources.error_default_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(error: Throwable?) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(Res.string.error)) },
        text = {
            Text(stringResource(Res.string.error_default_message))
               },//${if (isDebugMode) "\n${error!!.message}" else ""}") },
        confirmButton = {
            TextButton(onClick = {}) {
                Text(stringResource(Res.string.close))
            }
        }
    )
}