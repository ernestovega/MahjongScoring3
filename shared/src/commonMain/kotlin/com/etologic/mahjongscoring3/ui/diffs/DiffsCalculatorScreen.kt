package com.etologic.mahjongscoring3.ui.diffs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.domain.model.Diff
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.diff
import com.etologic.mahjongscoring3.resources.diffsCalculator
import com.etologic.mahjongscoring3.resources.direct
import com.etologic.mahjongscoring3.resources.i_think_is_enough_jajaja
import com.etologic.mahjongscoring3.resources.indirect
import com.etologic.mahjongscoring3.resources.self_pick
import com.etologic.mahjongscoring3.ui.common.MmTopBar
import com.etologic.mahjongscoring3.ui.common.TopBarNavigation
import com.etologic.mahjongscoring3.ui.main.LocalShowSnackbar
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

internal const val MIN_POINTS_NEEDED = 32
internal const val NUM_CALCS_INTERVAL = 200
internal const val MAX_INTERVALS = 10
internal const val MAX_ITEMS = NUM_CALCS_INTERVAL * MAX_INTERVALS

/** Row [index] of the calculator: MS2 starts at 32 points and grows up to [MAX_ITEMS] rows. */
internal fun diffsCalculatorRow(index: Int): Diff = Diff(MIN_POINTS_NEEDED + index)

@Composable
fun DiffsCalculatorScreen(onBack: () -> Unit) {
    val listState = rememberLazyListState()
    val showSnackbar = LocalShowSnackbar.current
    val enoughMessage = stringResource(Res.string.i_think_is_enough_jajaja)

    // When the user reaches the last row scrolling down, MS2 shows a funny message
    LaunchedEffect(listState) {
        snapshotFlow { !listState.canScrollForward && listState.lastScrolledForward }
            .distinctUntilChanged()
            .filter { it }
            .collect { showSnackbar(enoughMessage) }
    }

    Column(Modifier.fillMaxSize()) {
        MmTopBar(
            title = stringResource(Res.string.diffsCalculator),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBack,
        )
        Row(
            // MS2: the first cell has a 16dp start margin, outside of its weight: the 4 cells share the rest equally
            modifier = Modifier.fillMaxWidth().background(MmColors.Primary).padding(start = MmDimens.StandardMargin),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DiffsCell(stringResource(Res.string.diff), MmColors.White, bold = true, maxLines = 2)
            DiffsCell(stringResource(Res.string.self_pick), MmColors.White, bold = true)
            DiffsCell(stringResource(Res.string.direct), MmColors.White, bold = true)
            DiffsCell(stringResource(Res.string.indirect), MmColors.White, bold = true)
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().background(MmColors.Gray),
        ) {
            items(MAX_ITEMS) { index ->
                val diff = remember(index) { diffsCalculatorRow(index) }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 0) MmColors.White else MmColors.GrayLight)
                        .padding(start = MmDimens.StandardMargin)
                ) {
                    DiffsCell(diff.pointsNeeded.toString(), MmColors.GrayMM, bold = true)
                    DiffsCell(diff.selfPick.toString(), MmColors.GrayMM, bold = false)
                    DiffsCell(diff.directHu.toString(), MmColors.GrayMM, bold = false)
                    DiffsCell(diff.indirectHu.toString(), MmColors.GrayMM, bold = false)
                }
            }
        }
    }
}

/** MS2 "diffsCalculatorCell" style. */
@Composable
private fun RowScope.DiffsCell(
    text: String,
    color: Color,
    bold: Boolean,
    maxLines: Int = 1,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = color,
        fontSize = 16.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.weight(1f).padding(vertical = MmDimens.QuarterMargin),
    )
}
