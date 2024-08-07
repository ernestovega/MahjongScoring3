package screens.old_games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import screens.common.ui.GameId

@Immutable
data class OldGameItemState(
    val gameId: GameId,
    val oldGameItemHeaderState: OldGameItemHeaderState,
    val oldGameItemBodyState: OldGameItemBodyState,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OldGameItem(
    state: OldGameItemState,
    onClick: (gameId: GameId) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick(state.gameId) },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OldGameItemHeader(state.oldGameItemHeaderState)
            OldGameItemBody(state.oldGameItemBodyState)
        }
    }
}
