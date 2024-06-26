package com.etologic.mahjongscoring.screens.game.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import screens.game.table.GamePageTable

@Composable
@Preview
fun GamePageTablePreview(
    @PreviewParameter(GamePageListPreviewProvider::class) gamePageTableState: List<Pair<String, Int>>,
) {
    GamePageTable(gamePageTableState = gamePageTableState) {}
}

private class GamePageListPreviewProvider: PreviewParameterProvider<List<Pair<String, Int>>> {
    override val values = sequenceOf(
        listOf(
            "Eto" to 100,
            "Cris" to 100,
            "Maricarmen" to -100,
            "Covadonga" to -100,
        ),
    )
}