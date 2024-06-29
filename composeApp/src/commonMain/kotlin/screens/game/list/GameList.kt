package screens.game.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GamePageList(
    gamePageListState: List<Int>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GridItem(
            itemState = listOf(
                "#",
                "Pts",
                "Player1",
                "Player2",
                "Player3",
                "Player4",
            ) to emptyList(),
            backgroundColor = MaterialTheme.colors.primary,
            textColor = Color.White,
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(gamePageListState) { index, itemState ->
                GridItem(
                    itemState = listOf(
                        index.toString(),
                        itemState.toString(),
                        itemState.toString(),
                        itemState.toString(),
                        itemState.toString(),
                        itemState.toString(),
                    ) to listOf(
                        "",
                        "",
                        itemState.toString(),
                        itemState.toString(),
                        itemState.toString(),
                        itemState.toString(),
                    ),
                    backgroundColor = if (index % 2 == 0) MaterialTheme.colors.background else Color.LightGray,
                )
            }
        }
        GridItem(
            itemState = listOf(
                "Totals",
                "",
                gamePageListState.sum().toString(),
                gamePageListState.sum().toString(),
                gamePageListState.sum().toString(),
                gamePageListState.sum().toString(),
            ) to emptyList(),
            backgroundColor = MaterialTheme.colors.primary,
            textColor = Color.White,
        )
    }
}
