package screens.old_games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.best_hand
import mahjongscoring3.composeapp.generated.resources.date
import mahjongscoring3.composeapp.generated.resources.menu
import mahjongscoring3.composeapp.generated.resources.rounds
import org.jetbrains.compose.resources.stringResource
import screens.common.model.TableWinds
import screens.common.model.UiGame
import screens.common.ui.SmallSeatState
import screens.common.ui.SmallSeats
import screens.common.ui.SmallSeatsState
import screens.common.use_cases.utils.prettifyTwoLines

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

data class OldGameItemHeaderState(
    val gameName: String,
)

@Composable
private fun OldGameItemHeader(state: OldGameItemHeaderState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primarySurface),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp),
            text = state.gameName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                tint = Color.White,
                contentDescription = stringResource(Res.string.menu),
            )
        }
    }
}

data class OldGameItemBodyState(
    val smallSeatsState: SmallSeatsState,
    val oldGameItemBodyGameDataState: OldGameItemBodyGameDataState,
)

@Composable
private fun OldGameItemBody(state: OldGameItemBodyState) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        SmallSeats(
            modifier = Modifier
                .weight(.7f)
                .align(Alignment.CenterVertically),
            state = state.smallSeatsState,
        )
        OldGameItemBodyGameData(
            modifier = Modifier
                .weight(.3f)
                .align(Alignment.CenterVertically),
            state = state.oldGameItemBodyGameDataState,
        )
    }
}

data class OldGameItemBodyGameDataState(
    val date: Instant,
    val numRounds: Int,
    val bestHandPlayerName: String,
    val bestHandPoints: Int,
)

@Composable
private fun OldGameItemBodyGameData(
    state: OldGameItemBodyGameDataState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.date),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
            Text(
                text = state.date.prettifyTwoLines(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.rounds),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
            Text(
                text = state.toString(),
                fontSize = 12.sp,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.best_hand),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
            Text(
                text = "${state.bestHandPoints}\n${state.bestHandPlayerName}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}