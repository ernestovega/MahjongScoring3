package screens.old_games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OldGamesScreen(
    modifier: Modifier = Modifier,
    navigateToGame: () -> Unit,
) {
    val oldGames by remember { mutableStateOf(mutableListOf<Int>().apply { for (i in 1..10) add(i) }.toList()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(oldGames) { item ->
            OldGameItem(
                oldGameItemState = item to listOf(
                    "Ernesto Vega de la Iglesia" to 100,
                    "Cristina Gayol Miranda" to 100,
                    "Maricarmen Gutiérrez" to -100,
                    "Covadonga Jiménez" to -100,
                ),
                onClick = navigateToGame,
            )
        }
    }
}
