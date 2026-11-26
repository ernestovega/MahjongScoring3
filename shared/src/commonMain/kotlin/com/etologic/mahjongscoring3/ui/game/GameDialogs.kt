package com.etologic.mahjongscoring3.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.etologic.mahjongscoring3.domain.model.HuData
import com.etologic.mahjongscoring3.domain.model.PenaltyData
import com.etologic.mahjongscoring3.domain.model.RankingTableHelper
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MAX_MCR_POINTS
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MIN_MCR_POINTS
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.NUM_NO_WINNER_PLAYERS
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources._2nd
import com.etologic.mahjongscoring3.resources._3rd
import com.etologic.mahjongscoring3.resources._4th
import com.etologic.mahjongscoring3.resources.are_you_sure
import com.etologic.mahjongscoring3.resources.best_hand
import com.etologic.mahjongscoring3.resources.cancel_penalties
import com.etologic.mahjongscoring3.resources.close
import com.etologic.mahjongscoring3.resources.dice3droll
import com.etologic.mahjongscoring3.resources.discarder_
import com.etologic.mahjongscoring3.resources.divided
import com.etologic.mahjongscoring3.resources.draw
import com.etologic.mahjongscoring3.resources.final_results
import com.etologic.mahjongscoring3.resources.first
import com.etologic.mahjongscoring3.resources.five
import com.etologic.mahjongscoring3.resources.four
import com.etologic.mahjongscoring3.resources.ic_flag
import com.etologic.mahjongscoring3.resources.ic_trophy_golden
import com.etologic.mahjongscoring3.resources.ic_trophy_white
import com.etologic.mahjongscoring3.resources.ic_whistle
import com.etologic.mahjongscoring3.resources.mahjong
import com.etologic.mahjongscoring3.resources.ok
import com.etologic.mahjongscoring3.resources.one
import com.etologic.mahjongscoring3.resources.penalty
import com.etologic.mahjongscoring3.resources.points_
import com.etologic.mahjongscoring3.resources.resume
import com.etologic.mahjongscoring3.resources.save
import com.etologic.mahjongscoring3.resources.second
import com.etologic.mahjongscoring3.resources.share
import com.etologic.mahjongscoring3.resources.six
import com.etologic.mahjongscoring3.resources.three
import com.etologic.mahjongscoring3.resources.two
import com.etologic.mahjongscoring3.resources.winner_
import com.etologic.mahjongscoring3.resources.zero
import com.etologic.mahjongscoring3.ui.common.MmAlertDialog
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random
import com.etologic.mahjongscoring3.ui.common.rememberSecureClick

// HAND ACTIONS (MS2 HandActionsDialogFragment with its pages: actions, hu and penalty)

private enum class HandActionsPage { ACTIONS, HU, PENALTY }

/**
 * Full screen dialog shown when a seat is tapped. [selectedSeat] is the current wind of the seat.
 * Every callback that saves something also closes the dialog.
 */
@Composable
fun HandActionsDialog(
    game: UiGame,
    selectedSeat: TableWinds,
    isDiffsCalcsFeatureEnabled: Boolean,
    onHuDiscard: (HuData) -> Unit,
    onHuSelfPick: (HuData) -> Unit,
    onDraw: () -> Unit,
    onPenalty: (PenaltyData) -> Unit,
    onCancelPenalties: () -> Unit,
    onDismiss: () -> Unit,
) {
    var page by remember { mutableStateOf(HandActionsPage.ACTIONS) }
    val names = remember(game) { game.getPlayersNamesByCurrentSeat() }
    val selectedName = names.getOrElse(selectedSeat.code) { "" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MmColors.BlackTransparent)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = rememberSecureClick(onDismiss)),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                shape = RoundedCornerShape(MmDimens.CardCornerRadius),
                backgroundColor = MmColors.GrayLight,
                contentColor = MmColors.GrayMM,
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MmDimens.StandardMargin)
                    // Taps inside the card must not dismiss the dialog
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).padding(MmDimens.StandardMargin),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (page) {
                        HandActionsPage.ACTIONS -> HandActionsPageContent(
                            game = game,
                            selectedSeat = selectedSeat,
                            selectedName = selectedName,
                            isDiffsCalcsFeatureEnabled = isDiffsCalcsFeatureEnabled,
                            onHuClick = { page = HandActionsPage.HU },
                            onDrawClick = onDraw,
                            onPenaltyClick = { page = HandActionsPage.PENALTY },
                            onCancelPenalties = onCancelPenalties,
                        )

                        HandActionsPage.HU -> HuPageContent(
                            game = game,
                            selectedSeat = selectedSeat,
                            names = names,
                            onHuDiscard = onHuDiscard,
                            onHuSelfPick = onHuSelfPick,
                            onClose = onDismiss,
                        )

                        HandActionsPage.PENALTY -> PenaltyPageContent(
                            game = game,
                            selectedSeat = selectedSeat,
                            selectedName = selectedName,
                            onPenalty = onPenalty,
                            onClose = onDismiss,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandActionsPageContent(
    game: UiGame,
    selectedSeat: TableWinds,
    selectedName: String,
    isDiffsCalcsFeatureEnabled: Boolean,
    onHuClick: () -> Unit,
    onDrawClick: () -> Unit,
    onPenaltyClick: () -> Unit,
    onCancelPenalties: () -> Unit,
) {
    var showCancelPenaltiesConfirmation by remember { mutableStateOf(false) }

    SeatMedium(selectedSeat, selectedName)
    Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(top = MmDimens.HalfMargin))
    if (isDiffsCalcsFeatureEnabled) {
        val seatDiffs = remember(game, selectedSeat) { game.getTableDiffs().seatsDiffs[selectedSeat.code] }
        DiffsBigTable(seatDiffs, Modifier.fillMaxWidth().padding(top = MmDimens.StandardMargin, bottom = MmDimens.HalfMargin))
        Divider(
            color = MmColors.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(top = MmDimens.HalfMargin, bottom = MmDimens.StandardMargin),
        )
    } else {
        Spacer(Modifier.height(MmDimens.StandardMargin))
    }
    HandActionButton(
        icon = Res.drawable.ic_trophy_white,
        text = Res.string.mahjong,
        color = MmColors.Primary,
        fontSize = 21,
        isBig = true,
        tag = "btHandActionsDialogHu",
        onClick = onHuClick,
    )
    HandActionButton(
        icon = Res.drawable.ic_flag,
        text = Res.string.draw,
        color = MmColors.Accent,
        fontSize = 21,
        isBig = false,
        tag = "btHandActionsDialogDraw",
        onClick = onDrawClick,
    )
    HandActionButton(
        icon = Res.drawable.ic_whistle,
        text = Res.string.penalty,
        color = MmColors.PurplePenalty,
        fontSize = 18,
        isBig = false,
        tag = "btHandActionsDialogPenalty",
        onClick = onPenaltyClick,
    )
    if (game.ongoingRound.areTherePenalties()) {
        HandActionButton(
            icon = Res.drawable.ic_whistle,
            text = Res.string.cancel_penalties,
            color = MmColors.PurplePenalty,
            fontSize = 18,
            isBig = false,
            tag = "btHandActionsDialogPenaltiesCancel",
            onClick = { showCancelPenaltiesConfirmation = true },
        )
    }

    if (showCancelPenaltiesConfirmation) {
        MmAlertDialog(
            title = stringResource(Res.string.cancel_penalties),
            message = stringResource(Res.string.are_you_sure),
            confirmText = stringResource(Res.string.ok),
            onConfirm = {
                showCancelPenaltiesConfirmation = false
                onCancelPenalties()
            },
            dismissText = stringResource(Res.string.close),
            onDismiss = { showCancelPenaltiesConfirmation = false },
        )
    }
}

/** MS2 "ButtonHandActionBig" / "ButtonHandActionSmall": rounded colored button with an icon over a text. */
@Composable
private fun HandActionButton(
    icon: DrawableResource,
    text: StringResource,
    color: Color,
    fontSize: Int,
    isBig: Boolean,
    tag: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(MmDimens.HalfMargin),
        color = color,
        contentColor = Color.White,
        elevation = 4.dp,
        modifier = Modifier
            .testTag(tag)
            .fillMaxWidth()
            .padding(top = if (isBig) MmDimens.HalfMargin else MmDimens.HalfAndQuarterMargin),
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = rememberSecureClick(onClick))
                .padding(
                    horizontal = MmDimens.StandardAndHalfMargin,
                    vertical = if (isBig) MmDimens.StandardAndHalfMargin else MmDimens.HalfMargin,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = if (isBig) MmDimens.HalfAndQuarterMargin else 0.dp, bottom = MmDimens.QuarterMargin)
                    .size(24.dp), // MS2: wrap_content of a 24dp vector
            )
            Text(
                text = stringResource(text).uppercase(), // MS2: textAllCaps
                color = Color.White,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = if (isBig) MmDimens.HalfAndQuarterMargin else 0.dp),
            )
        }
    }
}

@Composable
private fun HuPageContent(
    game: UiGame,
    selectedSeat: TableWinds,
    names: List<String>,
    onHuDiscard: (HuData) -> Unit,
    onHuSelfPick: (HuData) -> Unit,
    onClose: () -> Unit,
) {
    // Same order as MS2: the three other seats from East to North
    val looserSeats = remember(selectedSeat) {
        listOf(
            if (selectedSeat == EAST) SOUTH else EAST,
            if (selectedSeat in listOf(EAST, SOUTH)) WEST else SOUTH,
            if (selectedSeat in listOf(EAST, SOUTH, WEST)) NORTH else WEST,
        )
    }
    var discarder by remember { mutableStateOf(NONE) }
    var points by remember { mutableStateOf("") }
    var isPointsError by remember { mutableStateOf(false) }

    Text(stringResource(Res.string.winner_), color = MmColors.GrayMM, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    SeatMedium(selectedSeat, names.getOrElse(selectedSeat.code) { "" })
    Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(top = MmDimens.HalfMargin))
    Text(
        text = stringResource(Res.string.discarder_),
        color = MmColors.GrayMM,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(MmDimens.HalfMargin),
    )
    DiscarderSelector(
        modifier = Modifier.testTag("cdsGameHuDialog"),
        seats = looserSeats.map { it to names.getOrElse(it.code) { "" } },
        selected = discarder,
        onSelect = { discarder = it },
    )
    Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(top = MmDimens.HalfMargin))
    Text(
        text = stringResource(Res.string.points_),
        color = MmColors.GrayMM,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = MmDimens.HalfMargin),
    )
    NumPad(
        modifier = Modifier.testTag("cnpGameHuDialog"),
        value = points,
        onValueChange = {
            points = it
            isPointsError = false
        },
        isError = isPointsError,
    )
    DialogButtons(
        onClose = onClose,
        closeTag = "btGameHuDialogCancel",
        confirmTag = "btGameHuDialogOk",
        confirmText = stringResource(Res.string.save),
        onConfirm = {
            val winnerHandPoints = points.toIntOrNull()
            if (winnerHandPoints == null || winnerHandPoints < MIN_MCR_POINTS || winnerHandPoints > MAX_MCR_POINTS) {
                isPointsError = true
            } else {
                val winnerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(selectedSeat)
                if (discarder == NONE) {
                    onHuSelfPick(HuData(points = winnerHandPoints, winnerInitialSeat = winnerInitialSeat))
                } else {
                    onHuDiscard(
                        HuData(
                            points = winnerHandPoints,
                            winnerInitialSeat = winnerInitialSeat,
                            discarderInitialSeat = game.getPlayerInitialSeatByCurrentSeat(discarder),
                        )
                    )
                }
            }
        },
    )
}

@Composable
private fun PenaltyPageContent(
    game: UiGame,
    selectedSeat: TableWinds,
    selectedName: String,
    onPenalty: (PenaltyData) -> Unit,
    onClose: () -> Unit,
) {
    var points by remember { mutableStateOf("") }
    var isPointsError by remember { mutableStateOf(false) }
    // MS2 layout: android:checked="true"
    var isDivided by remember { mutableStateOf(true) }
    var isDividedError by remember { mutableStateOf(false) }

    SeatMedium(selectedSeat, selectedName, Modifier.fillMaxWidth())
    Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(top = MmDimens.HalfMargin))
    Text(
        text = stringResource(Res.string.points_),
        color = MmColors.GrayMM,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = MmDimens.HalfMargin),
    )
    NumPad(
        modifier = Modifier.testTag("cnpPenaltyDialog"),
        value = points,
        onValueChange = {
            points = it
            isPointsError = false
            isDividedError = false
        },
        isError = isPointsError,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .testTag("cbPenaltyDialog")
            .padding(top = MmDimens.StandardMargin)
            .toggleable(value = isDivided, role = Role.Checkbox) { isDivided = it; isDividedError = false },
    ) {
        Checkbox(
            checked = isDivided,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MmColors.Accent,
                uncheckedColor = if (isDividedError) MmColors.Red else MmColors.GrayMM,
            ),
        )
        Text(stringResource(Res.string.divided), color = if (isDividedError) MmColors.Red else MmColors.GrayMM)
    }
    DialogButtons(
        onClose = onClose,
        closeTag = "btPenaltyDialogCancel",
        confirmTag = "btPenaltyDialogSave",
        confirmText = stringResource(Res.string.save),
        onConfirm = {
            val penaltyPoints = points.toIntOrNull() ?: 0
            when {
                penaltyPoints <= 0 -> isPointsError = true
                isDivided && penaltyPoints % NUM_NO_WINNER_PLAYERS != 0 -> {
                    isPointsError = true
                    isDividedError = true
                }

                else -> onPenalty(
                    PenaltyData(
                        points = penaltyPoints,
                        isDivided = isDivided,
                        penalizedPlayerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(selectedSeat),
                    )
                )
            }
        },
    )
}

@Composable
private fun DialogButtons(onClose: () -> Unit, confirmText: String, onConfirm: () -> Unit, closeTag: String, confirmTag: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = MmDimens.StandardMargin),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(onClick = rememberSecureClick(onClose), modifier = Modifier.testTag(closeTag)) {
            Text(stringResource(Res.string.close).uppercase(), color = MmColors.Accent)
        }
        TextButton(onClick = rememberSecureClick(onConfirm), modifier = Modifier.testTag(confirmTag)) {
            Text(confirmText.uppercase(), color = MmColors.Accent)
        }
    }
}

// RANKING (MS2 RankingDialogFragment)

@Composable
fun RankingDialog(
    game: UiGame,
    onResume: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
) {
    val rankingData = remember(game) { RankingTableHelper.generateRankingTable(game) } ?: return
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(MmDimens.CardCornerRadius),
            color = MmColors.GrayLight,
            contentColor = MmColors.GrayMM,
            modifier = Modifier.fillMaxWidth().padding(MmDimens.StandardMargin),
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // MS2 tlRanking: TableLayout padding 20dp, each TableRow margin 8dp
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(Res.string.final_results),
                        color = MmColors.GrayMM,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(MmDimens.HalfMargin).padding(bottom = 32.dp),
                    )
                    val positions = listOf(null, Res.string._2nd, Res.string._3rd, Res.string._4th)
                    rankingData.sortedPlayersRankings.forEachIndexed { index, player ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(MmDimens.HalfMargin),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.weight(2f), contentAlignment = Alignment.Center) {
                                val position = positions[index]
                                if (position == null) {
                                    Image(painterResource(Res.drawable.ic_trophy_golden), contentDescription = null, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(stringResource(position), color = MmColors.GrayMM, fontSize = 14.sp)
                                }
                            }
                            RankingCell(player.name, 6f, tag = "tvRankingDialogPlayer${index + 1}Name", fontSize = 18, horizontalMargin = MmDimens.StandardMargin)
                            RankingCell(player.points, 2f, tag = "tvRankingDialogPlayer${index + 1}Points", fontSize = 18)
                            RankingCell(formatScore(player.score), 3f, tag = "tvRankingDialogPlayer${index + 1}Score", fontSize = 14)
                        }
                    }
                }
                Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(MmDimens.StandardMargin))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = MmDimens.StandardMargin),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${stringResource(Res.string.best_hand)}:", color = MmColors.GrayMM, fontSize = 16.sp)
                    Text(
                        rankingData.bestHandPlayerPoints,
                        color = MmColors.GrayMM,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 32.dp).testTag("tvRankingDialogBestHandPlayerPoints"),
                    )
                    Text("-", color = MmColors.GrayMM, modifier = Modifier.padding(horizontal = MmDimens.StandardMargin))
                    Text(
                        rankingData.bestHandPlayerName,
                        color = MmColors.GrayMM,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false).testTag("tvRankingDialogBestHandPlayerName"),
                    )
                }
                Divider(color = MmColors.Gray, thickness = 1.dp, modifier = Modifier.padding(MmDimens.StandardMargin))
                Row(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = MmDimens.StandardMargin, bottom = MmDimens.StandardMargin),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (rankingData.numRounds < MAX_MCR_ROUNDS) {
                        TextButton(onClick = rememberSecureClick { onResume(); onDismiss() }, modifier = Modifier.testTag("btRankingDialogResume")) {
                            Text(stringResource(Res.string.resume).uppercase(), color = MmColors.Accent)
                        }
                        Spacer(Modifier.weight(1f))
                    }
                    TextButton(onClick = rememberSecureClick(action = onShare), modifier = Modifier.testTag("btRankingDialogShare")) {
                        Text(stringResource(Res.string.share).uppercase(), color = MmColors.Accent)
                    }
                    Spacer(Modifier.width(MmDimens.StandardMargin))
                    TextButton(onClick = rememberSecureClick(action = onDismiss), modifier = Modifier.testTag("btRankingDialogClose")) {
                        Text(stringResource(Res.string.close).uppercase(), color = MmColors.Accent)
                    }
                }
            }
        }
    }
}

/** MS2 TextViewRankingDialogTableContent: start-aligned, one line, ellipsized. */
@Composable
private fun RowScope.RankingCell(text: String, weight: Float, tag: String, fontSize: Int, horizontalMargin: Dp = 0.dp) {
    Text(
        text = text,
        color = MmColors.GrayMM,
        fontSize = fontSize.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(weight).padding(horizontal = horizontalMargin).testTag(tag),
    )
}

/** Same as String.format("%+d"). */
internal fun formatScore(score: Int): String = if (score >= 0) "+$score" else score.toString()

// DICE (MS2 DiceDialogFragment)

private const val DICE_ROLL_DELAY_MS = 400L
private val diceFaces = listOf(Res.drawable.one, Res.drawable.two, Res.drawable.three, Res.drawable.four, Res.drawable.five, Res.drawable.six)
private val diceDescriptions = listOf(Res.string.one, Res.string.two, Res.string.three, Res.string.four, Res.string.five, Res.string.six)

@Composable
fun DiceDialog(game: UiGame, playDiceSound: () -> Unit, onDismiss: () -> Unit) {
    // 0 = not rolled yet (rolling image)
    val dice = remember { mutableStateListOf(0, 0, 0, 0) }
    var rollingPair by remember { mutableIntStateOf(-1) } // -1 none, 0 dice 1-2, 1 dice 3-4
    var isSecondThrowVisible by remember { mutableStateOf(false) }
    var rollId by remember { mutableIntStateOf(0) }

    LaunchedEffect(rollId) {
        if (rollingPair < 0) return@LaunchedEffect
        delay(DICE_ROLL_DELAY_MS)
        val first = rollingPair * 2
        dice[first] = Random.nextInt(6) + 1
        dice[first + 1] = Random.nextInt(6) + 1
        if (rollingPair == 0) isSecondThrowVisible = true
        rollingPair = -1
    }

    fun roll(pair: Int) {
        if (rollingPair >= 0) return
        if (pair == 0) {
            for (i in 0..3) dice[i] = 0
        } else {
            dice[2] = 0
            dice[3] = 0
        }
        rollingPair = pair
        playDiceSound()
        playDiceSound()
        rollId++
    }

    val firstThrower = game.getCurrentEastSeatPlayerName() ?: stringResource(Res.string.first)
    val secondThrower = when (dice[0] + dice[1]) {
        5, 9 -> game.getCurrentEastSeatPlayerName()
        2, 6, 10 -> game.getCurrentSouthSeatPlayerName()
        3, 7, 11 -> game.getCurrentWestSeatPlayerName()
        4, 8, 12 -> game.getCurrentNorthSeatPlayerName()
        else -> null
    } ?: stringResource(Res.string.second)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(MmDimens.CardCornerRadius),
            color = MmColors.White,
            contentColor = MmColors.GrayMM,
            modifier = Modifier.fillMaxWidth().padding(MmDimens.StandardMargin),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DiceThrowerName(firstThrower, tag = "tvDiceDialogFirst")
                DicePair(dice[0], dice[1], isVisible = true, tags = "ivDice1" to "ivDice2") { roll(0) }
                DiceThrowerName(if (rollingPair != 0) secondThrower else "", isVisible = isSecondThrowVisible, tag = "tvDiceDialogSecond")
                DicePair(dice[2], dice[3], isVisible = isSecondThrowVisible, tags = "ivDice3" to "ivDice4") { roll(1) }
            }
        }
    }
}

@Composable
private fun DiceThrowerName(name: String, tag: String, isVisible: Boolean = true) {
    Text(
        text = if (isVisible) name else "",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MmColors.GrayMM,
        modifier = Modifier
            .testTag(tag)
            .fillMaxWidth()
            .padding(start = MmDimens.TripleMargin, top = MmDimens.TripleMargin),
    )
}

@Composable
private fun DicePair(value1: Int, value2: Int, isVisible: Boolean, tags: Pair<String, String>, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(
            start = MmDimens.TripleMargin,
            top = MmDimens.StandardMargin,
            end = MmDimens.TripleMargin,
            bottom = MmDimens.TripleMargin,
        ),
        horizontalArrangement = Arrangement.Center,
    ) {
        Die(value1, isVisible, onClick, Modifier.weight(1f).padding(end = 16.dp).testTag(tags.first))
        Die(value2, isVisible, onClick, Modifier.weight(1f).padding(start = 16.dp).testTag(tags.second))
    }
}

@Composable
private fun Die(value: Int, isVisible: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        if (isVisible) {
            Image(
                painter = painterResource(if (value == 0) Res.drawable.dice3droll else diceFaces[value - 1]),
                contentDescription = stringResource(if (value == 0) Res.string.zero else diceDescriptions[value - 1]),
                modifier = Modifier.size(96.dp).clickable(onClick = rememberSecureClick(action = onClick)),
            )
        } else {
            Spacer(Modifier.size(96.dp))
        }
    }
}
