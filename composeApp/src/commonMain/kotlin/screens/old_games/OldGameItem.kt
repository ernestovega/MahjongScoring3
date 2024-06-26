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
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.best_hand
import mahjongscoring3.composeapp.generated.resources.date
import mahjongscoring3.composeapp.generated.resources.menu
import mahjongscoring3.composeapp.generated.resources.rounds
import org.jetbrains.compose.resources.stringResource
import screens.common.Seats

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OldGameItem(
    oldGameItemState: Pair<Int, List<Pair<String, Int>>>,
    onClick: () -> Unit,
) {
    Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OldGameItemHeader(oldGameItemState.first)
            OldGameItemBody(oldGameItemState.second)
        }
    }
}

@Composable
private fun OldGameItemHeader(item: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primarySurface),
    ) {
        Text(
            text = "Game #$item",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                tint = Color.White,
                contentDescription = stringResource(Res.string.menu),
            )
        }
    }
}

@Composable
private fun OldGameItemBody(seatsState: List<Pair<String, Int>>) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Seats(
            seatsState = seatsState,
            modifier = Modifier
                .weight(.6f)
                .align(Alignment.CenterVertically),
        )
        OldGameItemBodyGameData(
            item = 8,
            modifier = Modifier
                .weight(.4f)
                .align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun OldGameItemBodyGameData(
    item: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.date),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "21/06/2024\n14:14",
                textAlign = TextAlign.Center,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.rounds),
                fontWeight = FontWeight.Bold,
            )
            Text(text = item.toString())
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.best_hand),
                fontWeight = FontWeight.Bold,
            )
            Text(text = "$item - Fulanito")
        }
    }
}