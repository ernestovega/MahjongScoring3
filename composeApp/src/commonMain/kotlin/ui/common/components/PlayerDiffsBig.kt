package ui.common.components

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
import domain.model.SeatDiffs
import domain.use_cases.utils.toStringOrHyphen

@Composable
fun PlayerDiffsBig(playerDiffs: SeatDiffs) {
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
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeFirst?.bySelfPick.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeFirst?.byDirectHu.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeFirst?.byIndirectHu.toStringOrHyphen())
        }
        Row {
            PlayerDiffsBigCellTitle("2nd")
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeSecond?.bySelfPick.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeSecond?.byDirectHu.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeSecond?.byIndirectHu.toStringOrHyphen())
        }
        Row {
            PlayerDiffsBigCellTitle("3rd")
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeThird?.bySelfPick.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeThird?.byDirectHu.toStringOrHyphen())
            PlayerDiffsBigCellBody(playerDiffs.pointsToBeThird?.byIndirectHu.toStringOrHyphen())
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
