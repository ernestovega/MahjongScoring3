package com.etologic.mahjongscoring3.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.domain.model.RankingTableHelper
import com.etologic.mahjongscoring3.domain.model.SeatDiffs
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.utils.formatSigned
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources._1st
import com.etologic.mahjongscoring3.resources._2nd
import com.etologic.mahjongscoring3.resources._3rd
import com.etologic.mahjongscoring3.resources._4th
import com.etologic.mahjongscoring3.resources.end
import com.etologic.mahjongscoring3.resources.ic_calc
import com.etologic.mahjongscoring3.resources.ic_dice
import com.etologic.mahjongscoring3.resources.ic_trophy_white
import com.etologic.mahjongscoring3.resources.throw_dice
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.etologic.mahjongscoring3.ui.common.rememberSecureClick

private enum class Corner { TOP_START, TOP_END, BOTTOM_START, BOTTOM_END }

/** Port of MS2 "game_table_fragment" + "GameTableSeats". */
@Composable
fun GameTable(
    game: UiGame,
    seatsArrangement: SeatsArrangement,
    isDiffsCalcsFeatureEnabled: Boolean,
    shouldShowDiffs: Boolean,
    selectedSeat: TableWinds,
    onSeatClick: (TableWinds) -> Unit,
    onToggleDiffs: (Boolean) -> Unit,
    onDiceClick: () -> Unit,
    onRankingClick: () -> Unit,
) {
    val states = remember(game, selectedSeat) {
        TableWinds.asArray.map { wind ->
            when {
                game.isEnded -> SeatState.DISABLED
                wind == selectedSeat -> SeatState.SELECTED
                else -> SeatState.NORMAL
            }
        }
    }
    val tableDiffs = remember(game) { game.getTableDiffs() }
    val showDiffs = isDiffsCalcsFeatureEnabled && shouldShowDiffs
    val fabCorner = if (game.isEnded) Corner.BOTTOM_END else when (game.uiRounds.size % 4) {
        1 -> Corner.BOTTOM_END
        2 -> Corner.TOP_END
        3 -> Corner.TOP_START
        else -> Corner.BOTTOM_START
    }

    Column(Modifier.fillMaxSize().background(MmColors.GrayLight)) {
        if (game.gameName.isNotEmpty()) {
            Text(
                text = game.gameName,
                color = MmColors.GrayDiffs,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(MmDimens.StandardMargin).testTag("tvGameTableGameName"),
            )
        }
        Box(Modifier.fillMaxSize()) {
            when (seatsArrangement) {
                SeatsArrangement.RANKING -> SeatsByRanking(
                    game = game,
                    states = states,
                    seatsDiffs = tableDiffs.seatsDiffs,
                    showDiffs = showDiffs,
                    isDiffsButtonVisible = isDiffsCalcsFeatureEnabled,
                    onSeatClick = { if (!game.isEnded) onSeatClick(it) },
                    onToggleDiffs = onToggleDiffs,
                )
                else -> SeatsBySeat(
                    game = game,
                    arrangement = seatsArrangement,
                    states = states,
                    seatsDiffs = tableDiffs.seatsDiffs,
                    showDiffs = showDiffs,
                    isDiffsButtonVisible = isDiffsCalcsFeatureEnabled,
                    onSeatClick = { if (!game.isEnded) onSeatClick(it) },
                    onToggleDiffs = onToggleDiffs,
                )
            }
            // Round number labels in the corners not used by the FAB
            Corner.entries.filter { it != fabCorner }.forEach { corner ->
                RoundNumberLabel(game, corner)
            }
            FloatingActionButton(
                onClick = rememberSecureClick(action = if (game.isEnded) onRankingClick else onDiceClick),
                backgroundColor = MmColors.Accent,
                contentColor = MmColors.White,
                modifier = Modifier
                    .align(fabCorner.alignment())
                    .padding(MmDimens.StandardMargin)
                    // MS2 asks for 64dp, but the FloatingActionButton (fabSize normal) draws at 56dp
                    .size(56.dp)
                    .testTag("fabGameTable"),
            ) {
                Icon(
                    painter = painterResource(if (game.isEnded) Res.drawable.ic_trophy_white else Res.drawable.ic_dice),
                    contentDescription = stringResource(Res.string.throw_dice),
                )
            }
        }
    }
}

private fun Corner.alignment(): Alignment =
    when (this) {
        Corner.TOP_START -> Alignment.TopStart
        Corner.TOP_END -> Alignment.TopEnd
        Corner.BOTTOM_START -> Alignment.BottomStart
        Corner.BOTTOM_END -> Alignment.BottomEnd
    }

@Composable
private fun BoxScope.RoundNumberLabel(game: UiGame, corner: Corner) {
    val numRounds = game.uiRounds.size
    val wind = when {
        game.isEnded -> TableWinds.NONE
        numRounds < 5 -> TableWinds.EAST
        numRounds < 9 -> TableWinds.SOUTH
        numRounds < 13 -> TableWinds.WEST
        else -> TableWinds.NORTH
    }
    val (rotation, padding) = when (corner) {
        Corner.TOP_START -> -215f to Modifier.padding(start = MmDimens.DoubleMargin, top = MmDimens.StandardMargin)
        Corner.TOP_END -> 215f to Modifier.padding(end = MmDimens.DoubleMargin, top = MmDimens.StandardMargin)
        Corner.BOTTOM_START -> 45f to Modifier.padding(start = MmDimens.DoubleMargin, bottom = MmDimens.StandardMargin)
        Corner.BOTTOM_END -> -45f to Modifier.padding(end = MmDimens.DoubleMargin, bottom = MmDimens.StandardMargin)
    }
    Column(
        modifier = Modifier
            .align(corner.alignment())
            .then(padding)
            .graphicsLayer(rotationZ = rotation),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (game.isEnded) stringResource(Res.string.end) else numRounds.toString(),
            color = MmColors.GrayMM,
            fontSize = MmDimens.TableRoundNumber,
            textAlign = TextAlign.Center,
        )
        if (wind != TableWinds.NONE) {
            Spacer(Modifier.height(MmDimens.HalfMargin))
            WindIcon(wind, MmColors.GrayMM)
        }
    }
}

/** The calculator button: shows the points differences while it is pressed. */
@Composable
private fun DiffsButton(isVisible: Boolean, onToggleDiffs: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            // MS2 hides the button with INVISIBLE: here the tag exists only while it is visible
            .then(if (isVisible) Modifier.testTag("btTableSeatsShowDiffs") else Modifier)
            .size(MmDimens.TripleMargin)
            .then(
                if (isVisible) {
                    Modifier.pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown()
                            onToggleDiffs(true)
                            waitForUpOrCancellation()
                            onToggleDiffs(false)
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isVisible) {
            Image(
                painter = painterResource(Res.drawable.ic_calc),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MmColors.Gray),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SeatsBySeat(
    game: UiGame,
    arrangement: SeatsArrangement,
    states: List<SeatState>,
    seatsDiffs: List<SeatDiffs>,
    showDiffs: Boolean,
    isDiffsButtonVisible: Boolean,
    onSeatClick: (TableWinds) -> Unit,
    onToggleDiffs: (Boolean) -> Unit,
) {
    val points = game.getPlayersTotalPointsByCurrentSeat()
    val winds = game.getSeatsCurrentWind(game.uiRounds.size)
    val names = game.getPlayersNamesByCurrentSeat()
    val penalties = game.getPlayersPenaltiesByCurrentSeat()
    val margin = MmDimens.StandardMargin

    BoxWithConstraints(Modifier.fillMaxSize()) {
    // Change from MS2: on short tables (phones in landscape) MS2 squeezes the West and East seats until only
    // their wind icon shows. Here every seat turns into one row (icon, points, penalty, name): North and South
    // beside the diffs button, West above it and East below it, so all four players stay visible.
    val isRow = maxHeight < COMPACT_TABLE_MAX_HEIGHT

    @Composable
    fun SeatAt(seat: TableWinds) {
        val index = seat.code
        val (rotation, paddingModifier) = when (arrangement) {
            // On short tables North and South rows are not turned: turned, they would be taller than the table
            SeatsArrangement.OUT -> when (seat) {
                TableWinds.EAST -> 0f to Modifier.padding(top = if (isRow) MmDimens.HalfMargin else margin)
                TableWinds.SOUTH -> if (isRow) 0f to Modifier.padding(horizontal = margin) else -90f to Modifier.padding(bottom = margin)
                TableWinds.WEST -> 180f to Modifier.padding(top = if (isRow) MmDimens.HalfMargin else margin)
                else -> if (isRow) 0f to Modifier.padding(horizontal = margin) else 90f to Modifier.padding(bottom = margin)
            }
            else -> when (seat) {
                TableWinds.EAST -> 0f to Modifier.padding(top = if (isRow) MmDimens.HalfMargin else margin * 3)
                TableWinds.WEST -> 0f to Modifier.padding(bottom = if (isRow) MmDimens.HalfMargin else margin * 3)
                else -> 0f to if (isRow) Modifier.padding(horizontal = margin) else Modifier
            }
        }
        SeatBySeat(
            wind = winds[index],
            points = points[index],
            penalty = penalties[index],
            name = names[index],
            state = states[index],
            isEnded = game.isEnded,
            seatDiffs = seatsDiffs.getOrNull(index)?.takeIf { showDiffs },
            isRow = isRow,
            onClick = { onSeatClick(seat) },
            modifier = Modifier
                .testTag("iGameTableSeat" + seat.name.lowercase().replaceFirstChar { it.uppercase() })
                .graphicsLayer(rotationZ = rotation)
                .then(paddingModifier),
        )
    }

    Layout(
        content = {
            DiffsButton(isDiffsButtonVisible, onToggleDiffs)
            SeatAt(TableWinds.EAST)
            SeatAt(TableWinds.SOUTH)
            SeatAt(TableWinds.WEST)
            SeatAt(TableWinds.NORTH)
        },
        modifier = Modifier.fillMaxSize().testTag("iTableSeatsBySeat"),
    ) { measurables, constraints ->
        // MS2 RelativeLayout: each seat only gets the space on its side of the diffs button (layout_above,
        // layout_below, layout_toRightOf, layout_toLeftOf). On short tables (landscape phones) the West and
        // East seats are squeezed like in MS2: the wind icon shrinks to fit and the rest gets no height.
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val button = measurables[0].measure(loose)
        val cx = width / 2
        val cy = height / 2
        val buttonLeft = cx - button.width / 2
        val buttonTop = cy - button.height / 2
        val buttonRight = buttonLeft + button.width
        val buttonBottom = buttonTop + button.height
        val east = measurables[1].measure(loose.copy(maxHeight = (height - buttonBottom).coerceAtLeast(0)))
        val south = measurables[2].measure(loose.copy(maxWidth = (width - buttonRight).coerceAtLeast(0)))
        val west = measurables[3].measure(loose.copy(maxHeight = buttonTop.coerceAtLeast(0)))
        val north = measurables[4].measure(loose.copy(maxWidth = buttonLeft.coerceAtLeast(0)))
        layout(width, height) {
            button.place(buttonLeft, buttonTop)
            east.place(cx - east.width / 2, buttonTop + button.height) // below the button
            west.place(cx - west.width / 2, buttonTop - west.height) // above the button
            south.place(buttonLeft + button.width, cy - south.height / 2) // right of the button
            north.place(buttonLeft - north.width, cy - north.height / 2) // left of the button
        }
    }
    }
}

/**
 * Below this table height the full West and East seats (about 86dp each, plus 48dp of margin) do not fit above
 * and below the 48dp diffs button, so they turn into rows. Phones in landscape have about 175dp; portrait
 * phones and tablets have more than 500dp.
 */
private val COMPACT_TABLE_MAX_HEIGHT = 320.dp

/** MS2 "custom_table_seat_by_seat". */
@Composable
private fun SeatBySeat(
    wind: TableWinds,
    points: Int,
    penalty: Int,
    name: String,
    state: SeatState,
    isEnded: Boolean,
    seatDiffs: SeatDiffs?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRow: Boolean = false,
) {
    if (isRow) {
        SeatBySeatRow(wind, points, penalty, name, state, isEnded, seatDiffs, onClick, modifier)
        return
    }
    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = rememberSecureClick(action = onClick),
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WindIcon(wind, state.color)
        Text(
            text = points.toString(),
            color = state.color,
            fontSize = MmDimens.TablePlayerPoints,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("tvTableSeatBySeatPoints"),
        )
        PenaltyPoints(penalty, isEnded, Modifier.offset(y = (-8).dp))
        Text(
            text = name,
            color = state.color,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(164.dp).testTag("tvTableSeatBySeatName"),
        )
        seatDiffs?.let { DiffsSmallTable(it) }
    }
}

/** A seat in one row, for short tables (see [COMPACT_TABLE_MAX_HEIGHT]). Same texts and test tags as [SeatBySeat]. */
@Composable
private fun SeatBySeatRow(
    wind: TableWinds,
    points: Int,
    penalty: Int,
    name: String,
    state: SeatState,
    isEnded: Boolean,
    seatDiffs: SeatDiffs?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = rememberSecureClick(action = onClick),
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WindIcon(wind, state.color)
            Spacer(Modifier.width(MmDimens.StandardMargin))
            Text(
                text = points.toString(),
                color = state.color,
                fontSize = MmDimens.TablePlayerPoints,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.testTag("tvTableSeatBySeatPoints"),
            )
            PenaltyPoints(penalty, isEnded, Modifier.padding(start = MmDimens.HalfMargin))
            Spacer(Modifier.width(MmDimens.StandardMargin))
            Text(
                text = name,
                color = state.color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 164.dp).testTag("tvTableSeatBySeatName"),
            )
        }
        seatDiffs?.let { DiffsSmallTable(it) }
    }
}

@Composable
private fun PenaltyPoints(penalty: Int, isEnded: Boolean, modifier: Modifier = Modifier) {
    if (!isEnded && penalty != 0) {
        Text(
            text = penalty.formatSigned(),
            color = if (penalty < 0) MmColors.Red else MmColors.Primary,
            fontSize = MmDimens.TablePlayerPenaltyPoints,
            modifier = modifier,
        )
    }
}

@Composable
private fun SeatsByRanking(
    game: UiGame,
    states: List<SeatState>,
    seatsDiffs: List<SeatDiffs>,
    showDiffs: Boolean,
    isDiffsButtonVisible: Boolean,
    onSeatClick: (TableWinds) -> Unit,
    onToggleDiffs: (Boolean) -> Unit,
) {
    val rankingData = remember(game) { RankingTableHelper.generateRankingTable(game) } ?: return
    val namesByCurrentSeat = game.getPlayersNamesByCurrentSeat()
    val penaltiesByCurrentSeat = game.getPlayersPenaltiesByCurrentSeat()
    val positions = listOf(Res.string._1st, Res.string._2nd, Res.string._3rd, Res.string._4th)

    @Composable
    fun RankingSeat(index: Int, isFirstInColumn: Boolean, modifier: Modifier = Modifier) {
        val playerRanking = rankingData.sortedPlayersRankings[index]
        val seat = TableWinds.from(namesByCurrentSeat.indexOf(playerRanking.name)) ?: return
        if (seat == TableWinds.NONE) return
        val state = states[seat.code]
        Column(
            modifier = modifier
                .padding(top = if (isFirstInColumn) 0.dp else MmDimens.StandardMargin)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = rememberSecureClick { onSeatClick(seat) },
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(positions[index]),
                    color = MmColors.GrayMM,
                    fontSize = MmDimens.TablePlayerPenaltyPoints,
                    modifier = Modifier.padding(start = MmDimens.StandardMargin),
                )
                WindIcon(seat, state.color, modifier = Modifier.padding(start = MmDimens.StandardMargin), size = 32.dp)
                Text(
                    text = playerRanking.score.toString(),
                    color = state.color,
                    fontSize = MmDimens.TablePlayerPoints,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(start = MmDimens.StandardMargin),
                )
                PenaltyPoints(penaltiesByCurrentSeat[seat.code], game.isEnded, Modifier.offset(y = (-8).dp))
                Text(
                    text = playerRanking.name,
                    color = state.color,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = MmDimens.StandardMargin),
                )
            }
            if (showDiffs) {
                seatsDiffs.getOrNull(seat.code)?.let { DiffsSmallTable(it) }
            }
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxHeight < COMPACT_TABLE_MAX_HEIGHT) {
            // Change from MS2: on short tables (phones in landscape) the four rows do not fit one below the other,
            // so 1st and 3rd go on the left of the diffs button and 2nd and 4th on its right.
            val count = rankingData.sortedPlayersRankings.size
            Row(
                modifier = Modifier.fillMaxSize().testTag("iTableSeatsByRanking"),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    (0 until count step 2).forEach { RankingSeat(it, isFirstInColumn = it == 0) }
                }
                DiffsButton(isDiffsButtonVisible, onToggleDiffs, Modifier.padding(horizontal = MmDimens.StandardMargin))
                Column(horizontalAlignment = Alignment.Start) {
                    (1 until count step 2).forEach { RankingSeat(it, isFirstInColumn = it == 1) }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().testTag("iTableSeatsByRanking").verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                rankingData.sortedPlayersRankings.indices.forEach {
                    RankingSeat(it, isFirstInColumn = it == 0, Modifier.fillMaxWidth())
                }
                DiffsButton(isDiffsButtonVisible, onToggleDiffs, Modifier.padding(top = MmDimens.DoubleMargin))
            }
        }
    }
}

