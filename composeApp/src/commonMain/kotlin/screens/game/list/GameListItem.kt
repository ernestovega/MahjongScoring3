package screens.game.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GridItem(
    itemState: Pair<List<String>, List<String>>,
    backgroundColor: Color,
    textColor: Color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            itemState.first.forEachIndexed { index, item ->
                GridItemTextBox(
                    text = item,
                    weight = if (index <= 1) .5f else 1f,
                    fontWeight = FontWeight.Normal,
                    textColor = textColor,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            itemState.second.forEachIndexed { index, item ->
                GridItemTextBox(
                    text = item,
                    weight = if (index <= 1) .5f else 1f,
                    fontWeight = FontWeight.Bold,
                    textColor = textColor,
                )
            }
        }
    }
}

@Composable
fun RowScope.GridItemTextBox(
    text: String,
    weight: Float,
    fontWeight: FontWeight?,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = fontWeight,
        )
    }
}