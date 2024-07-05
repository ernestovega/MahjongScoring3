package screens.help.diffs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.diff
import mahjongscoring3.composeapp.generated.resources.direct
import mahjongscoring3.composeapp.generated.resources.indirect
import mahjongscoring3.composeapp.generated.resources.self_pick
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiffsScreenHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(
            Res.string.diff,
            Res.string.self_pick,
            Res.string.direct,
            Res.string.indirect,
        ).forEach { title ->
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(title),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}