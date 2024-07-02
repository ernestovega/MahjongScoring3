package screens.help.diffs

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
import screens.help.diffs.model.Diff

@Composable
fun DiffsScreenItem(
    state: Diff,
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
        listOf(
            state.pointsNeeded,
            state.selfPick,
            state.directHu,
            state.indirectHu,
        ).forEach { value ->
            Text(
                modifier = Modifier.weight(1f),
                text = value.toString(),
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}