package screens.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import screens.help.diffs.Diff

@Composable
fun PlayerDiffsBig(playerDiffs: Diff) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            PlayerDiffsBigCellTitle("")
            PlayerDiffsBigCellTitle("Self pick")
            PlayerDiffsBigCellTitle("Direct")
            PlayerDiffsBigCellTitle("Indirect")
        }
        Row {
            PlayerDiffsBigCellTitle("1st")
            PlayerDiffsBigCellBody("23")
            PlayerDiffsBigCellBody("45")
            PlayerDiffsBigCellBody("89")
        }
        Row {
            PlayerDiffsBigCellTitle("2nd")
            PlayerDiffsBigCellBody("8")
            PlayerDiffsBigCellBody("8")
            PlayerDiffsBigCellBody("8")
        }
        Row {
            PlayerDiffsBigCellTitle("3rd")
            PlayerDiffsBigCellBody("-")
            PlayerDiffsBigCellBody("-")
            PlayerDiffsBigCellBody("-")
        }
    }
}

@Composable
private fun RowScope.PlayerDiffsBigCellTitle(text: String) {
    Text(
        modifier = Modifier.weight(1f),
        text = text,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun RowScope.PlayerDiffsBigCellBody(text: String = "-") {
    Text(
        modifier = Modifier.weight(1f),
        text = text,
        textAlign = TextAlign.Center,
    )
}
