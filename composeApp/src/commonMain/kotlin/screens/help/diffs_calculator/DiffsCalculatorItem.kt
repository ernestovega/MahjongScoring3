package screens.help.diffs_calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DiffsCalculatorItem(
    diffsCalculatorItemState: Int,
    backgroundColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background),
    height: Dp = 24.dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) {
            Text(
                modifier = Modifier.weight(1f),
                text = diffsCalculatorItemState.toString(),
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}