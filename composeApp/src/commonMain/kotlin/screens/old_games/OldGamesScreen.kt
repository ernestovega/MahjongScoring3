package screens.old_games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

data class OldGamesScreenState(
    val gamesStates: List<OldGameItemState> = emptyList(),
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun OldGamesScreen(
    viewModel: OldGamesScreenViewModel = koinViewModel<OldGamesScreenViewModel>(),
    navigateToGame: () -> Unit,
) {
    val state by viewModel.screenStateFlow.collectAsState()

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
}
