package screens.old_games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.best_hand
import mahjongscoring3.composeapp.generated.resources.date
import mahjongscoring3.composeapp.generated.resources.rounds
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import screens.common.ui.SmallSeats
import screens.common.ui.SmallSeatsState
import screens.common.use_cases.utils.prettifyTwoLines

@Immutable
data class OldGameItemBodyState(
    val smallSeatsState: SmallSeatsState,
    val oldGameItemBodyInfoState: OldGameItemBodyInfoState,
)

@Composable
fun OldGameItemBody(state: OldGameItemBodyState) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(horizontal = 16.dp, vertical = 24.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SmallSeats(
            modifier = Modifier.weight(.7f),
            state = state.smallSeatsState,
        )
        OldGameItemBodyInfo(
            modifier = Modifier.weight(.3f),
            state = state.oldGameItemBodyInfoState,
        )
    }
}

@Immutable
data class OldGameItemBodyInfoState(
    val date: Instant,
    val numRounds: Int,
    val bestHandPoints: Int,
    val bestHandPlayerName: String,
)

@Composable
fun OldGameItemBodyInfo(
    state: OldGameItemBodyInfoState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OldGameItemBodyInfoBlock(
            title = Res.string.date,
            body = state.date.prettifyTwoLines(),
        )
        OldGameItemBodyInfoBlock(
            title = Res.string.rounds,
            body = state.numRounds.toString(),
        )
        OldGameItemBodyInfoBlock(
            title = Res.string.best_hand,
            body = "${state.bestHandPoints}\n${state.bestHandPlayerName}",
        )
    }
}

@Composable
private fun OldGameItemBodyInfoBlock(
    title: StringResource,
    body: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body2,
        )
        Text(
            text = body,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
        )
    }
}