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
                    .size(MmDimens.QuadrupleMargin)
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

    @Composable
    fun SeatAt(seat: TableWinds) {
        val index = seat.code
        val (rotation, paddingModifier) = when (arrangement) {
            SeatsArrangement.OUT -> when (seat) {
                TableWinds.EAST -> 0f to Modifier.padding(top = margin)
                TableWinds.SOUTH -> -90f to Modifier.padding(bottom = margin)
                TableWinds.WEST -> 180f to Modifier.padding(top = margin)
                else -> 90f to Modifier.padding(bottom = margin)
            }
            else -> when (seat) {
                TableWinds.EAST -> 0f to Modifier.padding(top = margin * 3)
                TableWinds.WEST -> 0f to Modifier.padding(bottom = margin * 3)
                else -> 0f to Modifier
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
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(loose) }
        val button = placeables[0]
        val east = placeables[1]
        val south = placeables[2]
        val west = placeables[3]
        val north = placeables[4]
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        layout(width, height) {
            val cx = width / 2
            val cy = height / 2
            val buttonLeft = cx - button.width / 2
            val buttonTop = cy - button.height / 2
            button.place(buttonLeft, buttonTop)
            east.place(cx - east.width / 2, buttonTop + button.height) // below the button
            west.place(cx - west.width / 2, buttonTop - west.height) // above the button
            south.place(buttonLeft + button.width, cy - south.height / 2) // right of the button
            north.place(buttonLeft - north.width, cy - north.height / 2) // left of the button
        }
    }
}

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
) {
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

    Column(
        modifier = Modifier.fillMaxSize().testTag("iTableSeatsByRanking").verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        rankingData.sortedPlayersRankings.forEachIndexed { index, playerRanking ->
            val seat = TableWinds.from(namesByCurrentSeat.indexOf(playerRanking.name)) ?: return@forEachIndexed
            if (seat == TableWinds.NONE) return@forEachIndexed
            val state = states[seat.code]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (index == 0) 0.dp else MmDimens.StandardMargin)
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
        DiffsButton(isDiffsButtonVisible, onToggleDiffs, Modifier.padding(top = MmDimens.DoubleMargin))
    }
}

