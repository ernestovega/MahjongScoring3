package screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.pts
import mahjongscoring3.composeapp.generated.resources.totals
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class GamePageListHeaderState(
    val playerNameEastSeat: String = "",
    val playerNameSouthSeat: String = "",
    val playerNameWestSeat: String = "",
    val playerNameNorthSeat: String = "",
)

@Composable
fun GamePageListHeader(state: GamePageListHeaderState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GamePageListHeaderCell("#", Modifier.weight(.5f))
        GamePageListHeaderCell(stringResource(Res.string.pts), Modifier.weight(.5f))
        GamePageListHeaderCell(state.playerNameEastSeat)
        GamePageListHeaderCell(state.playerNameSouthSeat)
        GamePageListHeaderCell(state.playerNameWestSeat)
        GamePageListHeaderCell(state.playerNameNorthSeat)
    }
}

@Composable
private fun RowScope.GamePageListHeaderCell(
    text: String,
    modifier: Modifier = Modifier.weight(1f),
) {
    Box(
        modifier = modifier.padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
