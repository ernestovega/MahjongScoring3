package screens.old_games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.menu
import org.jetbrains.compose.resources.stringResource

@Immutable
data class OldGameItemHeaderState(
    val gameName: String,
)

@Composable
fun OldGameItemHeader(state: OldGameItemHeaderState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp),
            text = state.gameName,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(Res.string.menu),
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
}
