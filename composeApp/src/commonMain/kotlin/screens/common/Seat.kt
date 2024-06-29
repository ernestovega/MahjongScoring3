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
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageVector = Icons.Filled.Home,
            contentDescription = stringResource(Res.string.west),
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = seatState.first,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = seatState.second.toString(),
        )
    }
}