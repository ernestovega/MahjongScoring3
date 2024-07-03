package screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.penalties
import mahjongscoring3.composeapp.generated.resources.pts
import mahjongscoring3.composeapp.generated.resources.totals
import org.jetbrains.compose.resources.stringResource
import screens.common.use_cases.utils.toSignedString

data class GamePageListState(
    val gamePageListHeaderState: GamePageListHeaderState = GamePageListHeaderState(),
    val roundsStates: List<GamePageListItemState> = emptyList(),
    val gamePageListFooterState: GamePageListFooterState = GamePageListFooterState(),
)

@Composable
fun GamePageList(state: GamePageListState) {
    Column(modifier = Modifier.fillMaxSize()) {
        GamePageListHeader(state = state.gamePageListHeaderState)
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(state.roundsStates) { index, roundState ->
                GamePageListItem(
                    state = roundState,
                    modifier = Modifier.background(if (index % 2 == 0) Color.White else Color.LightGray),
                )
            }
        }
        GamePageListFooter(state = state.gamePageListFooterState)
    }
}

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
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = "#")
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = stringResource(Res.string.pts))
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.playerNameEastSeat)
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.playerNameSouthSeat)
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.playerNameWestSeat)
        }
        Column(modifier = Modifier.weight(1f)) {
            GamePageListItemCell(text = state.playerNameNorthSeat)
        }
    }
}

data class GamePageListFooterState(
    val totalPointsPlayerEastSeat: Int = 0,
    val totalPointsPlayerSouthSeat: Int = 0,
    val totalPointsPlayerWestSeat: Int = 0,
    val totalPointsPlayerNorthSeat: Int = 0,
    val penaltiesPlayerEastSeat: Int = 0,
    val penaltiesPlayerSouthSeat: Int = 0,
    val penaltiesPlayerWestSeat: Int = 0,
    val penaltiesPlayerNorthSeat: Int = 0,
) {
    val areTherePenalties = penaltiesPlayerEastSeat != 0 ||
            penaltiesPlayerSouthSeat != 0 ||
            penaltiesPlayerWestSeat != 0 ||
            penaltiesPlayerNorthSeat != 0
}

@Composable
fun GamePageListFooter(state: GamePageListFooterState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.weight(2f)) {
            if (state.areTherePenalties) {
                GamePageListItemCell(text = stringResource(Res.string.penalties))
            }
            GamePageListItemCell(text = stringResource(Res.string.totals))
        }
        Column(modifier = Modifier.weight(1f)) {
            if (state.areTherePenalties) {
                GamePageListItemCell(text = state.penaltiesPlayerEastSeat.toSignedString())
            }
            GamePageListItemCell(text = state.totalPointsPlayerEastSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            if (state.areTherePenalties) {
                GamePageListItemCell(text = state.penaltiesPlayerSouthSeat.toSignedString())
            }
            GamePageListItemCell(text = state.totalPointsPlayerSouthSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            if (state.areTherePenalties) {
                GamePageListItemCell(text = state.penaltiesPlayerWestSeat.toSignedString())
            }
            GamePageListItemCell(text = state.totalPointsPlayerWestSeat.toSignedString())
        }
        Column(modifier = Modifier.weight(1f)) {
            if (state.areTherePenalties) {
                GamePageListItemCell(text = state.penaltiesPlayerNorthSeat.toSignedString())
            }
            GamePageListItemCell(text = state.totalPointsPlayerNorthSeat.toSignedString())
        }
    }
}
