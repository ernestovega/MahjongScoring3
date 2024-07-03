package screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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

    val penaltiesEastSeat: Int = 0,
    val penaltiesSouthSeat: Int = 0,
    val penaltiesWestSeat: Int = 0,
    val penaltiesNorthSeat: Int = 0,

    val totalPointsEastSeat: Int = 0,
    val totalPointsSouthSeat: Int = 0,
    val totalPointsWestSeat: Int = 0,
    val totalPointsNorthSeat: Int = 0,
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
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.roundNum.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.handPoints.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.roundPointsEastSeat.toSignedString())
            GamePageListItemCell(text = state.penaltiesEastSeat.toSignedString())
            GamePageListItemCell(text = state.totalPointsEastSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.roundPointsSouthSeat.toSignedString())
            GamePageListItemCell(text = state.penaltiesSouthSeat.toSignedString())
            GamePageListItemCell(text = state.totalPointsSouthSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.roundPointsWestSeat.toSignedString())
            GamePageListItemCell(text = state.penaltiesWestSeat.toSignedString())
            GamePageListItemCell(text = state.totalPointsWestSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.roundPointsNorthSeat.toSignedString())
            GamePageListItemCell(text = state.penaltiesNorthSeat.toSignedString())
            GamePageListItemCell(text = state.totalPointsNorthSeat.toSignedString())
        }
    }
}

@Composable
fun ColumnScope.GamePageListItemCell(
    text: String = "",
    weight: Float = 1f,
    fontWeight: FontWeight = FontWeight.Normal,
    backgroundColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.contentColorFor(backgroundColor),
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
        )
    }
}