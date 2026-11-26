package com.etologic.mahjongscoring3.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.platform.PlatformActions
import com.etologic.mahjongscoring3.platform.rememberDiceSoundPlayer
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.are_you_sure
import com.etologic.mahjongscoring3.resources.close
import com.etologic.mahjongscoring3.resources.delete_round
import com.etologic.mahjongscoring3.resources.disable_diffs_calcs
import com.etologic.mahjongscoring3.resources.edit_names
import com.etologic.mahjongscoring3.resources.enable_diffs_calcs
import com.etologic.mahjongscoring3.resources.end
import com.etologic.mahjongscoring3.resources.game
import com.etologic.mahjongscoring3.resources.ic_orientation_down
import com.etologic.mahjongscoring3.resources.ic_orientation_out
import com.etologic.mahjongscoring3.resources.ic_trophy_white
import com.etologic.mahjongscoring3.resources.list
import com.etologic.mahjongscoring3.resources.ok
import com.etologic.mahjongscoring3.resources.resume
import com.etologic.mahjongscoring3.resources.rotate_seats
import com.etologic.mahjongscoring3.resources.save
import com.etologic.mahjongscoring3.resources.share
import com.etologic.mahjongscoring3.resources.share_files_using
import com.etologic.mahjongscoring3.resources.share_game_using
import com.etologic.mahjongscoring3.resources.table
import com.etologic.mahjongscoring3.ui.common.ErrorDialog
import com.etologic.mahjongscoring3.ui.common.GameNames
import com.etologic.mahjongscoring3.ui.common.MmAlertDialog
import com.etologic.mahjongscoring3.ui.common.MmTopBar
import com.etologic.mahjongscoring3.ui.common.NamesDialog
import com.etologic.mahjongscoring3.ui.common.OverflowItem
import com.etologic.mahjongscoring3.ui.common.OverflowMenu
import com.etologic.mahjongscoring3.ui.common.TopBarNavigation
import com.etologic.mahjongscoring3.ui.oldgames.ShareGameDialog
import com.etologic.mahjongscoring3.ui.oldgames.defaultPlayerNames
import com.etologic.mahjongscoring3.ui.oldgames.withDefaultPlayerNames
import com.etologic.mahjongscoring3.ui.theme.MmColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Port of MS2 GameFragment: toolbar menu, tabs Table/List and every game dialog. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    gameId: GameId,
    platformActions: PlatformActions,
    onOpenDrawer: () -> Unit,
    viewModel: GameViewModel = koinViewModel(),
) {
    LaunchedEffect(gameId) { if (gameId != NOT_SET_GAME_ID) viewModel.setGameId(gameId) }

    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()
    val shouldShowDiffs by viewModel.shouldShowDiffs.collectAsState()
    val selectedSeat by viewModel.selectedSeat.collectAsState()
    val pageEvent by viewModel.pageEvent.collectAsState()

    val pagerState = rememberPagerState(initialPage = GamePages.TABLE.index) { GamePages.entries.size }
    val scope = rememberCoroutineScope()
    fun showPage(page: GamePages) {
        if (pagerState.currentPage != page.index) scope.launch { pagerState.animateScrollToPage(page.index) }
    }

    var showRanking by remember { mutableStateOf(false) }
    var showDice by remember { mutableStateOf(false) }
    var showEditNames by remember { mutableStateOf(false) }
    var showShare by remember { mutableStateOf(false) }
    var roundToDelete by remember { mutableStateOf<RoundId?>(null) }

    val shareGameUsing = stringResource(Res.string.share_game_using)
    val shareFilesUsing = stringResource(Res.string.share_files_using)
    val playDiceSound = rememberDiceSoundPlayer()

    // One-shot events after saving a hand
    var highlightLastRoundEventId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(pageEvent) {
        when (val event = pageEvent) {
            is GamePageEvent.HighlightLastRound -> {
                highlightLastRoundEventId = event.id
                showPage(GamePages.LIST)
                viewModel.consumePageEvent(event)
            }

            is GamePageEvent.ShowRanking -> {
                showPage(GamePages.TABLE)
                showRanking = true
                viewModel.consumePageEvent(event)
            }

            null -> {}
        }
    }

    // In the list, back goes to the table (MS2 onBackOrUpClick)
    BackHandler(enabled = pagerState.currentPage == GamePages.LIST.index) {
        showPage(GamePages.TABLE)
    }

    val loaded = uiState as? GameUiState.Loaded

    Column(Modifier.fillMaxSize()) {
        MmTopBar(
            title = stringResource(Res.string.game),
            navigation = TopBarNavigation.DRAWER,
            onNavigationClick = onOpenDrawer,
            actions = {
                if (loaded != null) {
                    IconButton(
                        onClick = {
                            showPage(GamePages.TABLE)
                            viewModel.nextSeatsOrientation()
                        },
                        modifier = Modifier.testTag("action_seats_orientation"),
                    ) {
                        Icon(
                            painter = painterResource(
                                when (loaded.seatsArrangement) {
                                    SeatsArrangement.DOWN -> Res.drawable.ic_orientation_down
                                    SeatsArrangement.OUT -> Res.drawable.ic_orientation_out
                                    SeatsArrangement.RANKING -> Res.drawable.ic_trophy_white
                                }
                            ),
                            contentDescription = stringResource(Res.string.rotate_seats),
                            tint = MmColors.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    OverflowMenu(gameMenuItems(loaded, viewModel, platformActions,
                        onEditNames = { showEditNames = true },
                        onShare = { showShare = true },
                    ))
                }
            },
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MmColors.Primary,
            contentColor = MmColors.White,
            indicator = { positions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(positions[pagerState.currentPage]),
                    height = 4.dp,
                    color = MmColors.Accent,
                )
            },
            modifier = Modifier.testTag("tabLayoutGame"),
        ) {
            listOf(GamePages.TABLE to Res.string.table, GamePages.LIST to Res.string.list).forEach { (page, title) ->
                Tab(
                    modifier = Modifier.testTag("tabGame" + page.name),
                    selected = pagerState.currentPage == page.index,
                    onClick = { showPage(page) },
                    text = { Text(stringResource(title).uppercase(), color = MmColors.White) },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().background(MmColors.GrayLight).testTag("viewPagerGame"),
            beyondViewportPageCount = 1,
        ) { page ->
            Box(Modifier.fillMaxSize()) {
                if (loaded != null) {
                    when (page) {
                        GamePages.TABLE.index -> GameTable(
                            game = loaded.game,
                            seatsArrangement = loaded.seatsArrangement,
                            isDiffsCalcsFeatureEnabled = loaded.isDiffsCalcsFeatureEnabled,
                            shouldShowDiffs = shouldShowDiffs,
                            selectedSeat = selectedSeat,
                            onSeatClick = viewModel::onSeatClicked,
                            onToggleDiffs = viewModel::toggleDiffsView,
                            onDiceClick = { showDice = true },
                            onRankingClick = { showRanking = true },
                        )

                        else -> GameList(
                            game = loaded.game,
                            highlightLastRoundEvent = highlightLastRoundEventId,
                            onHighlightConsumed = { highlightLastRoundEventId = null },
                            onDeleteRoundClick = { roundToDelete = it },
                        )
                    }
                }
            }
        }
    }

    // DIALOGS

    if (loaded != null) {
        val game = loaded.game

        if (selectedSeat != TableWinds.NONE && !game.isEnded) {
            HandActionsDialog(
                game = game,
                selectedSeat = selectedSeat,
                isDiffsCalcsFeatureEnabled = loaded.isDiffsCalcsFeatureEnabled,
                onHuDiscard = viewModel::saveHuDiscardRound,
                onHuSelfPick = viewModel::saveHuSelfPickRound,
                onDraw = viewModel::saveDrawRound,
                onPenalty = viewModel::savePenalty,
                onCancelPenalties = viewModel::cancelPenalties,
                onDismiss = viewModel::unselectSeats,
            )
        }

        if (showRanking) {
            RankingDialog(
                game = game,
                onResume = viewModel::resumeGame,
                onShare = { showShare = true },
                onDismiss = { showRanking = false },
            )
        }

        if (showDice) {
            DiceDialog(game = game, playDiceSound = playDiceSound, onDismiss = { showDice = false })
        }

        if (showEditNames) {
            EditNamesDialog(
                game = game,
                onSave = { names ->
                    viewModel.saveGameNames(names)
                    showEditNames = false
                },
                onDismiss = { showEditNames = false },
            )
        }
    }

    if (showShare) {
        ShareGameDialog(
            onOptionSelected = { option ->
                viewModel.shareGame(
                    option = option,
                    showShareText = { platformActions.shareText(it, shareGameUsing) },
                    showShareFile = { platformActions.shareFile(it, shareFilesUsing) },
                )
            },
            onDismiss = { showShare = false },
        )
    }

    roundToDelete?.let { roundId ->
        MmAlertDialog(
            title = stringResource(Res.string.delete_round),
            message = stringResource(Res.string.are_you_sure),
            confirmText = stringResource(Res.string.ok),
            onConfirm = {
                viewModel.removeRound(roundId)
                roundToDelete = null
            },
            dismissText = stringResource(Res.string.close),
            onDismiss = { roundToDelete = null },
        )
    }

    error?.let { ErrorDialog(it, onDismiss = viewModel::dismissError) }
}

@Composable
private fun gameMenuItems(
    state: GameUiState.Loaded,
    viewModel: GameViewModel,
    platformActions: PlatformActions,
    onEditNames: () -> Unit,
    onShare: () -> Unit,
): List<OverflowItem> {
    val game = state.game
    val items = mutableListOf(OverflowItem(stringResource(Res.string.edit_names), "action_edit_names", onEditNames))
    items += if (state.isDiffsCalcsFeatureEnabled) {
        OverflowItem(stringResource(Res.string.disable_diffs_calcs), "action_disable_diffs_calcs") { viewModel.setDiffsCalcsFeatureEnabled(false) }
    } else {
        OverflowItem(stringResource(Res.string.enable_diffs_calcs), "action_enable_diffs_calcs") { viewModel.setDiffsCalcsFeatureEnabled(true) }
    }
    items += OverflowItem(stringResource(Res.string.share), "action_share_game", onShare)
    if (game.isEnded && game.uiRounds.size < UiGame.MAX_MCR_ROUNDS) {
        items += OverflowItem(stringResource(Res.string.resume), "action_resume_game") { viewModel.resumeGame() }
    }
    if (!game.isEnded && game.uiRounds.size > 1) {
        items += OverflowItem(stringResource(Res.string.end), "action_end_game") { viewModel.endGame { platformActions.requestInAppReview() } }
    }
    return items
}

/** MS2 EditNamesDialogFragment: the setup dialog with the current names and a "Save" button. */
@Composable
private fun EditNamesDialog(game: UiGame, onSave: (GameNames) -> Unit, onDismiss: () -> Unit) {
    val defaults = defaultPlayerNames()
    NamesDialog(
        initial = GameNames(
            gameName = game.gameName,
            nameP1 = game.nameP1,
            nameP2 = game.nameP2,
            nameP3 = game.nameP3,
            nameP4 = game.nameP4,
        ),
        gameNameHint = null,
        confirmText = stringResource(Res.string.save),
        onConfirm = { onSave(it.withDefaultPlayerNames(defaults)) },
        onDismiss = onDismiss,
    )
}
