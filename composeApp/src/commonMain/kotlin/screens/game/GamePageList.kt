package screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class GamePageListState(
    val gamePageListHeaderState: GamePageListHeaderState = GamePageListHeaderState(),
    val roundsStates: List<GamePageListItemState> = emptyList(),
    val gamePageListPenaltiesFooterState: GamePageListFooterState? = null,
    val gamePageListTotalsFooterState: GamePageListFooterState = GamePageListFooterState(),
)

@Composable
fun GamePageList(state: GamePageListState) {
    Column(modifier = Modifier.fillMaxSize()) {

        GamePageListHeader(state = state.gamePageListHeaderState)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(state.roundsStates) { index, roundState ->
                GamePageListItem(
                    state = roundState,
                    modifier = Modifier.background(if (index % 2 == 0) Color.White else Color.LightGray),
                )
            }
        }

        if (state.gamePageListPenaltiesFooterState != null) {
            GamePageListTotalsFooter(
                state = state.gamePageListPenaltiesFooterState,
                modifier = Modifier.background(MaterialTheme.colors.onSurface),
            )
        }


        GamePageListTotalsFooter(
            state = state.gamePageListTotalsFooterState,
            modifier = Modifier.background(MaterialTheme.colors.primarySurface),
        )
    }
}
