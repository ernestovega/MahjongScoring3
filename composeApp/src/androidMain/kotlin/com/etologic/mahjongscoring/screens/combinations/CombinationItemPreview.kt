package com.etologic.mahjongscoring.screens.combinations

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import screens.combinations.CombinationItem

@Composable
@Preview
fun CombinationItemPreview(
    @PreviewParameter(CombinationItemPreviewProvider::class) combinationItem: Int) {
    CombinationItem(combinationItem) {}
}

private class CombinationItemPreviewProvider: PreviewParameterProvider<Int> {
    override val values = sequenceOf(1,2,3)
}
