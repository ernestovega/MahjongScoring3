package ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.use_cases.utils.toSignedString
import ui.common.AppColors.greenMM
import ui.common.AppColors.red
import ui.common.components.applyIf

@Immutable
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
    val secondaryRowBgColor = MaterialTheme.colors.onPrimary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .applyIf(state.roundNum % 2 == 0) { background(secondaryRowBgColor) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GamePageListItemCell(state.roundNum, FontWeight.Normal, Modifier.weight(.5f))
        GamePageListItemCell(state.handPoints, modifier = Modifier.weight(.5f))
        GamePageListItemCell(state.roundPointsEastSeat, state.penaltiesEastSeat, state.roundTotalPointsEastSeat, state.isEastWinner, state.isEastLooser)
        GamePageListItemCell(state.roundPointsSouthSeat, state.penaltiesSouthSeat, state.roundTotalPointsSouthSeat, state.isSouthWinner, state.isSouthLooser)
        GamePageListItemCell(state.roundPointsWestSeat, state.penaltiesWestSeat, state.roundTotalPointsWestSeat, state.isWestWinner, state.isWestLooser)
        GamePageListItemCell(state.roundPointsNorthSeat, state.penaltiesNorthSeat, state.roundTotalPointsNorthSeat, state.isNorthWinner, state.isNorthLooser)
    }
}

@Composable
private fun RowScope.GamePageListItemCell(
    text: Int,
    fontWeight: FontWeight = FontWeight.Bold,
    modifier: Modifier = Modifier.weight(1f),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GamePageListItemCellText(text = text, shouldBeSigned = false, fontWeight = fontWeight)
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
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GamePageListItemCellText(text = roundPoints, isWinner = isWinner, isLooser = isLooser)
            GamePageListItemCellText(text = roundTotalPoints, fontWeight = FontWeight.Normal)
        }

        roundPenalty?.let { GamePageListItemCellTextSmall(it) }
    }
}

@Composable
private fun ColumnScope.GamePageListItemCellText(
    text: Int,
    shouldBeSigned: Boolean = true,
    isWinner: Boolean = false,
    isLooser: Boolean = false,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    Text(
        modifier = Modifier.padding(4.dp),
        text = if (shouldBeSigned) text.toSignedString() else text.toString(),
        color = when {
            isWinner -> greenMM
            isLooser -> red
            else -> MaterialTheme.colors.onSurface
        },
        fontWeight = fontWeight,
    )
}

@Composable
private fun GamePageListItemCellTextSmall(text: Int) {
    Text(
        modifier = Modifier.padding(start = 24.dp, bottom = 2.dp),
        text = text.toSignedString(),
        color = if (text < 0) red else greenMM,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
    )
}
