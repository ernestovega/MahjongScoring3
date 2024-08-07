package screens.old_games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.common.ui.GameId

@Immutable
data class OldGamesScreenState(
    val gamesStates: List<OldGameItemState> = emptyList(),
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun OldGamesScreen(
    viewModel: OldGamesScreenViewModel = koinViewModel<OldGamesScreenViewModel>(),
    navigateToGame: (gameId: GameId) -> Unit,
    openCreateGameDialog: () -> Unit,
) {
    val state by viewModel.screenStateFlow.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.gamesStates) { gameState ->
                OldGameItem(
                    state = gameState,
                    onClick = navigateToGame,
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = openCreateGameDialog,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(Res.string.create_game),
            )
        }
    }
}
