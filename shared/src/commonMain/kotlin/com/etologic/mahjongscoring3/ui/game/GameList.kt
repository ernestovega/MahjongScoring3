package com.etologic.mahjongscoring3.ui.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiRound
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.utils.formatSigned
import com.etologic.mahjongscoring3.domain.utils.toStringSigned
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.arrow
import com.etologic.mahjongscoring3.resources.delete
import com.etologic.mahjongscoring3.resources.ic_trophy_golden
import com.etologic.mahjongscoring3.resources.ic_whistle
import com.etologic.mahjongscoring3.resources.img_empty_box
import com.etologic.mahjongscoring3.resources.number_symbol
import com.etologic.mahjongscoring3.resources.pts
import com.etologic.mahjongscoring3.resources.totals
import com.etologic.mahjongscoring3.ui.common.ArrowHeight
import com.etologic.mahjongscoring3.ui.common.ArrowWidth
import com.etologic.mahjongscoring3.ui.common.EmptyBoxHeight
import com.etologic.mahjongscoring3.ui.common.EmptyBoxWidth
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.etologic.mahjongscoring3.ui.common.rememberSecureClick

/** Port of MS2 "game_list_fragment". */
@Composable
fun GameList(
    game: UiGame,
    highlightLastRoundEvent: Long?,
    onHighlightConsumed: () -> Unit,
    onDeleteRoundClick: (RoundId) -> Unit,
) {
    val rounds = game.finishedRounds
    val listState = rememberLazyListState()
    var highlightedRoundId by remember { mutableStateOf<RoundId?>(null) }

    LaunchedEffect(rounds.size) {
        if (rounds.isNotEmpty()) listState.animateScrollToItem(rounds.size - 1)
    }
    LaunchedEffect(highlightLastRoundEvent) {
        if (highlightLastRoundEvent != null && rounds.isNotEmpty()) {
            delay(300)
            listState.animateScrollToItem(rounds.size - 1)
            highlightedRoundId = rounds.last().roundId
            onHighlightConsumed()
        }
    }

    Column(Modifier.fillMaxSize().background(MmColors.GrayLight)) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MmColors.Primary)
                .padding(horizontal = MmDimens.StandardMargin, vertical = MmDimens.HalfMargin),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(Res.string.number_symbol), color = Color.White, textAlign = TextAlign.Center, maxLines = 1,
                modifier = Modifier.width(16.dp).padding(end = 0.dp))
            Spacer(Modifier.width(MmDimens.HalfMargin))
            TwoLinesHeaderCell(stringResource(Res.string.pts), bold = false, modifier = Modifier.width(MmDimens.DoubleMargin))
            Spacer(Modifier.width(MmDimens.HalfMargin))
            game.playersNames.forEachIndexed { index, name ->
                TwoLinesHeaderCell(name, bold = true, modifier = Modifier.weight(1f))
                if (index < 3) Spacer(Modifier.width(MmDimens.HalfMargin))
            }
        }
        // ROUNDS
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (rounds.isEmpty()) {
                GameListEmptyState()
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(rounds, key = { _, round -> round.roundId }) { _, round ->
                        GameListItem(
                            round = round,
                            highlight = highlightedRoundId == round.roundId,
                            onHighlightFinished = { highlightedRoundId = null },
                            onDeleteClick = { onDeleteRoundClick(round.roundId) },
                        )
                    }
                }
            }
        }
        // FOOTER
        val ongoing = game.ongoingRound
        if (ongoing.areTherePenalties()) {
            FooterRow(MmColors.PurplePenalty, 16.sp, FontWeight.Normal,
                first = {
                    Box(Modifier.width(MmDimens.QuadrupleMargin), contentAlignment = Alignment.Center) {
                        Image(painterResource(Res.drawable.ic_whistle), null, colorFilter = ColorFilter.tint(Color.White))
                    }
                },
                values = listOf(ongoing.penaltyP1, ongoing.penaltyP2, ongoing.penaltyP3, ongoing.penaltyP4).map { it.toStringSigned() },
            )
        }
        FooterRow(MmColors.Primary, 18.sp, FontWeight.Bold,
            first = {
                Text(stringResource(Res.string.totals), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, maxLines = 1, modifier = Modifier.width(MmDimens.QuadrupleMargin))
            },
            values = listOf(ongoing.totalPointsP1, ongoing.totalPointsP2, ongoing.totalPointsP3, ongoing.totalPointsP4).map { it.toStringSigned() },
        )
    }
}

/** MS2 header TextViews use android:lines="2": always two lines tall, text centered. */
@Composable
private fun TwoLinesHeaderCell(text: String, bold: Boolean, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text("", minLines = 2, maxLines = 2)
        Text(
            text,
            color = Color.White,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FooterRow(
    background: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    first: @Composable () -> Unit,
    values: List<String>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = MmDimens.StandardMargin, vertical = MmDimens.HalfMargin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        first()
        values.forEachIndexed { index, value ->
            Text(value, color = Color.White, fontSize = fontSize, fontWeight = fontWeight, textAlign = TextAlign.Center,
                maxLines = 1, modifier = Modifier.weight(1f))
            if (index < 3) Spacer(Modifier.width(MmDimens.HalfMargin))
        }
    }
}

@Composable
private fun GameListEmptyState() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.arrow),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(end = ArrowWidth + 10.dp, bottom = ArrowHeight - 32.dp)
                .size(ArrowWidth, ArrowHeight)
                .graphicsLayer(rotationZ = 160f),
        )
        Image(
            painter = painterResource(Res.drawable.img_empty_box),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = EmptyBoxHeight)
                .size(EmptyBoxWidth, EmptyBoxHeight),
        )
    }
}

@Composable
private fun GameListItem(
    round: UiRound,
    highlight: Boolean,
    onHighlightFinished: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val baseColor = if (round.roundNumber % 2 == 0) MmColors.GrayLight else MmColors.White
    val background = remember(round.roundId) { Animatable(baseColor) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(baseColor) { background.snapTo(baseColor) }
    LaunchedEffect(highlight) {
        if (highlight) {
            // Same as MS2: blink 3 times with the accent color during 1.5s
            background.animateTo(
                targetValue = baseColor,
                animationSpec = keyframes {
                    durationMillis = 1500
                    MmColors.Accent at 250 using LinearEasing
                    baseColor at 500 using LinearEasing
                    MmColors.Accent at 750 using LinearEasing
                    baseColor at 1000 using LinearEasing
                    MmColors.Accent at 1250 using LinearEasing
                },
            )
            onHighlightFinished()
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("llGameListItemContainer")
                .background(background.value)
                .combinedClickable(onClick = rememberSecureClick { showMenu = true }, onLongClick = { showMenu = true })
                .padding(horizontal = MmDimens.StandardMargin, vertical = MmDimens.HalfMargin),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(round.roundNumber.toString(), color = MmColors.GrayMM, textAlign = TextAlign.Center, maxLines = 1,
                    modifier = Modifier.width(with(LocalDensity.current) { 16.sp.toDp() }).testTag("tvGameListItemRoundNumber"))
                Spacer(Modifier.width(MmDimens.HalfMargin))
                Row(Modifier.width(MmDimens.DoubleAndHalfMargin), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                    Text(round.handPoints.toString(), color = MmColors.GrayMM, fontSize = 16.sp, maxLines = 1)
                    if (round.isBestHand) {
                        Image(painterResource(Res.drawable.ic_trophy_golden), null, modifier = Modifier.size(16.dp))
                    }
                }
                val points = listOf(round.pointsP1, round.pointsP2, round.pointsP3, round.pointsP4)
                val penalties = listOf(round.penaltyP1, round.penaltyP2, round.penaltyP3, round.penaltyP4)
                TableWinds.asArray.forEachIndexed { index, seat ->
                    val color = when (seat) {
                        round.discarderInitialSeat -> MmColors.Red
                        round.winnerInitialSeat -> MmColors.Primary
                        else -> MmColors.GrayMM
                    }
                    // MS2: points centered in the cell, whistle to their end aligned to the top
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.Top,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                        if (penalties[index] < 0) Spacer(Modifier.width(MmDimens.StandardMargin))
                        Text(points[index].formatSigned(), color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        if (penalties[index] < 0) {
                            Image(painterResource(Res.drawable.ic_whistle), null, colorFilter = ColorFilter.tint(MmColors.PurplePenalty),
                                modifier = Modifier.size(MmDimens.StandardMargin))
                        }
                    }
                    if (index < 3) Spacer(Modifier.width(MmDimens.HalfMargin))
                }
            }
            if (round.areTherePenalties()) {
                Row {
                    // MS2: an empty 14sp TextView keeps this row one 14sp line tall
                    Text("", maxLines = 1, modifier = Modifier.width(MmDimens.StandardMargin + MmDimens.HalfMargin + MmDimens.DoubleMargin))
                    listOf(round.penaltyP1, round.penaltyP2, round.penaltyP3, round.penaltyP4).forEach { penalty ->
                        TotalsCell(penalty.toStringSigned(), if (penalty < 0) MmColors.Red else MmColors.Primary, 12.sp, TextAlign.End,
                            Modifier.padding(end = MmDimens.HalfAndQuarterMargin))
                    }
                }
            }
            Row(Modifier.padding(top = MmDimens.HalfMargin)) {
                Spacer(Modifier.width(MmDimens.StandardMargin + MmDimens.HalfMargin + MmDimens.DoubleMargin + MmDimens.HalfMargin))
                listOf(round.totalPointsP1, round.totalPointsP2, round.totalPointsP3, round.totalPointsP4).forEachIndexed { index, total ->
                    TotalsCell(total.formatSigned(), MmColors.GrayDiffs, 14.sp, TextAlign.Center,
                        if (index < 3) Modifier.padding(end = MmDimens.HalfMargin) else Modifier)
                }
            }
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(onClick = { showMenu = false; onDeleteClick() }, modifier = Modifier.testTag("action_delete_round")) {
                Text(stringResource(Res.string.delete), color = MmColors.GrayMM)
            }
        }
    }
}

@Composable
private fun RowScope.TotalsCell(text: String, color: Color, fontSize: TextUnit, align: TextAlign, modifier: Modifier) {
    Text(text, color = color, fontSize = fontSize, textAlign = align, maxLines = 1, modifier = modifier.weight(1f))
}
