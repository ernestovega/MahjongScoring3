package ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.use_cases.utils.toSignedString
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.totals
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
data class GamePageListFooterState(
    val title: StringResource = Res.string.totals,
    val pointsPlayerEastSeat: Int = 0,
    val pointsPlayerSouthSeat: Int = 0,
    val pointsPlayerWestSeat: Int = 0,
    val pointsPlayerNorthSeat: Int = 0,
)

@Composable
fun GamePageListTotalsFooter(
    state: GamePageListFooterState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GamePageListFooterCell(stringResource(state.title))
        GamePageListFooterCell(state.pointsPlayerEastSeat.toSignedString())
        GamePageListFooterCell(state.pointsPlayerSouthSeat.toSignedString())
        GamePageListFooterCell(state.pointsPlayerWestSeat.toSignedString())
        GamePageListFooterCell(state.pointsPlayerNorthSeat.toSignedString())
    }
}

@Composable
private fun RowScope.GamePageListFooterCell(text: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
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
