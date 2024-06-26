package screens.old_games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OldGamesScreen(
    modifier: Modifier = Modifier,
    navigateToGame: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) { item ->
            OldGameItem(
                oldGameItemState = item to listOf(
                    "Eto" to 100,
                    "Cris" to 100,
                    "Maricarmen" to -100,
                    "Covadonga" to -100
                ),
                onClick = navigateToGame,
            )
        }
    }
}
