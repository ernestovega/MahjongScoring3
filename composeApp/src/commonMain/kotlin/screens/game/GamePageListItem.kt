package screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import screens.common.use_cases.utils.toSignedString

data class GamePageListItemState(
    val roundNum: Int = 0,
    val handPoints: Int = 0,
    val isBestHand: Boolean = false,

    val roundPointsEastSeat: Int = 0,
    val roundPointsSouthSeat: Int = 0,
    val roundPointsWestSeat: Int = 0,
    val roundPointsNorthSeat: Int = 0,

    val penaltiesEastSeat: Int? = null,
    val penaltiesSouthSeat: Int? = null,
    val penaltiesWestSeat: Int? = null,
    val penaltiesNorthSeat: Int? = null,

    val roundTotalPointsEastSeat: Int = 0,
    val roundTotalPointsSouthSeat: Int = 0,
    val roundTotalPointsWestSeat: Int = 0,
    val roundTotalPointsNorthSeat: Int = 0,
)

@Composable
fun GamePageListItem(
    state: GamePageListItemState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        GamePageListItemCell(text = state.roundNum)
        GamePageListItemCell(text = state.handPoints)
        GamePageListItemCell(
            roundPoints = state.roundPointsEastSeat,
            roundPenalty = state.penaltiesEastSeat,
            roundTotalPoints = state.roundTotalPointsEastSeat,
        )
        GamePageListItemCell(
            roundPoints = state.roundPointsSouthSeat,
            roundPenalty = state.penaltiesSouthSeat,
            roundTotalPoints = state.roundTotalPointsSouthSeat,
        )
        GamePageListItemCell(
            roundPoints = state.roundPointsWestSeat,
            roundPenalty = state.penaltiesWestSeat,
            roundTotalPoints = state.roundTotalPointsWestSeat,
        )
        GamePageListItemCell(
            roundPoints = state.roundPointsNorthSeat,
            roundPenalty = state.penaltiesNorthSeat,
            roundTotalPoints = state.roundTotalPointsNorthSeat,
        )
    }
}

@Composable
private fun RowScope.GamePageListItemCell(text: Int) {
    Column(modifier = Modifier.weight(1f)) {
        GamePageListItemCellText(text = text)
    }
}

@Composable
private fun RowScope.GamePageListItemCell(
    roundPoints: Int,
    roundPenalty: Int?,
    roundTotalPoints: Int,
) {
    Column(modifier = Modifier.weight(1f)) {
        GamePageListItemCellText(text = roundPoints)
        roundPenalty?.let { GamePageListItemCellText(text = it) }
        GamePageListItemCellText(text = roundTotalPoints)
    }
}


@Composable
private fun ColumnScope.GamePageListItemCellText(
    text: Int,
    shouldBeColored: Boolean = true,
) {
    val defaultColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background)
    val positiveColor = Color.Green
    val negativeColor = Color.Red

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.toSignedString(),
            color = when {
                !shouldBeColored -> defaultColor
                text < 0 -> negativeColor
                else -> positiveColor
            },
//            textAlign = TextAlign.Center,
        )
    }
}
