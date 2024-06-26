package screens.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource

@Composable
fun Seat(
    seatState: Pair<String, Int>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = stringResource(Res.string.west),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = seatState.first,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = seatState.second.toString(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}