package ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.model.enums.TableWinds
import kotlinx.serialization.Serializable
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.west
import org.jetbrains.compose.resources.stringResource

@Immutable
@Serializable
data class SeatState(
    val wind: TableWinds = TableWinds.NONE,
    val name: String = "",
    val points: Int = 0,
)

@Composable
fun SmallSeat(
    state: SeatState,
    onClick: ((SeatState) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(96.dp)
            .applyIfNotNull(onClick) { clickable { it(state) } },
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageVector = Icons.Filled.Home,
            contentDescription = stringResource(Res.string.west),
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            text = state.name,
            maxLines = 1,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = state.points.toString(),
        )
    }
}