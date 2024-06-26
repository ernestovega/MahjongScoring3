package com.etologic.mahjongscoring.screens.game.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import screens.game.list.GamePageList

@Composable
@Preview
fun GamePageListPreview(
    @PreviewParameter(GamePageListPreviewProvider::class) gamePageListState: List<Int>,
) {
    GamePageList(gamePageListState = gamePageListState)
}

private class GamePageListPreviewProvider: PreviewParameterProvider<List<Int>> {
    override val values = sequenceOf(
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16),
    )
}