package screens.old_games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class OldGameItemState(
    val oldGameItemHeaderState: OldGameItemHeaderState,
    val oldGameItemBodyState: OldGameItemBodyState,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OldGameItem(
    state: OldGameItemState,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OldGameItemHeader(state.oldGameItemHeaderState)
            OldGameItemBody(state.oldGameItemBodyState)
        }
    }
}
