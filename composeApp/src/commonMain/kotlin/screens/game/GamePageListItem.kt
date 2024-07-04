package screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import screens.common.ui.NEGATIVE_COLOR
import screens.common.ui.POSITIVE_COLOR
import screens.common.use_cases.utils.toSignedString

data class GamePageListItemState(
    val roundNum: Int = 0,
    val handPoints: Int = 0,
    val isBestHand: Boolean = false,

    val isEastWinner: Boolean = false,
    val isSouthWinner: Boolean = false,
    val isWestWinner: Boolean = false,
    val isNorthWinner: Boolean = false,

    val isEastLooser: Boolean = false,
    val isSouthLooser: Boolean = false,
    val isWestLooser: Boolean = false,
    val isNorthLooser: Boolean = false,

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
        GamePageListItemCell(state.roundNum)
        GamePageListItemCell(state.handPoints)
        GamePageListItemCell(state.roundPointsEastSeat, state.penaltiesEastSeat, state.roundTotalPointsEastSeat, state.isEastWinner, state.isEastLooser)
        GamePageListItemCell(state.roundPointsSouthSeat, state.penaltiesSouthSeat, state.roundTotalPointsSouthSeat, state.isSouthWinner, state.isSouthLooser)
        GamePageListItemCell(state.roundPointsWestSeat, state.penaltiesWestSeat, state.roundTotalPointsWestSeat, state.isWestWinner, state.isWestLooser)
        GamePageListItemCell(state.roundPointsNorthSeat, state.penaltiesNorthSeat, state.roundTotalPointsNorthSeat, state.isNorthWinner, state.isNorthLooser)
    }
}

@Composable
private fun RowScope.GamePageListItemCell(text: Int) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GamePageListItemCellText(text = text, shouldBeSigned = false)
    }
}

@Composable
private fun RowScope.GamePageListItemCell(
    roundPoints: Int,
    roundPenalty: Int?,
    roundTotalPoints: Int,
    isWinner: Boolean,
    isLooser: Boolean,
) {
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        Column {
            GamePageListItemCellText(text = roundPoints, isWinner = isWinner, isLooser = isLooser)
            GamePageListItemCellText(text = roundTotalPoints)
        }

        if (roundPenalty != null) {
            GamePageListItemCellTextSmall(text = roundPenalty)
        }
    }
}

@Composable
private fun GamePageListItemCellText(
    text: Int,
    shouldBeSigned: Boolean = true,
    isWinner: Boolean = false,
    isLooser: Boolean = false,
) {
    val defaultColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background)

    Box(modifier = Modifier.padding(4.dp)) {
        Text(
            text = if (shouldBeSigned) text.toSignedString() else text.toString(),
            color = when {
                isWinner -> POSITIVE_COLOR
                isLooser -> NEGATIVE_COLOR
                else -> defaultColor
            },
        )
    }
}

@Composable
private fun GamePageListItemCellTextSmall(text: Int) {
    Box(modifier = Modifier.padding(start = 24.dp, bottom = 2.dp)) {
        Text(
            text =text.toSignedString(),
            color = if (text < 0) NEGATIVE_COLOR else POSITIVE_COLOR,
            fontSize = MaterialTheme.typography.caption.fontSize,
        )
    }
}
