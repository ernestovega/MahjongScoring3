package com.etologic.mahjongscoring.screens.old_games

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import screens.old_games.OldGameItem

@Composable
@Preview
fun OldGameItemPreview(
    @PreviewParameter(OldGameItemPreviewProvider::class) oldGameItemState: Pair<Int, List<Pair<String, Int>>>,
) {
    OldGameItem(oldGameItemState = oldGameItemState) {}
}

private class OldGameItemPreviewProvider: PreviewParameterProvider<Pair<Int, List<Pair<String, Int>>>> {
    override val values = sequenceOf(
        1 to listOf(
            "Eto" to 100,
            "Cris" to 100,
            "Maricarmen" to -100,
            "Covadonga" to -100
        ),
    )
}