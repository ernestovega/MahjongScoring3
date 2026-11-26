package com.etologic.mahjongscoring3.ui.oldgames

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.RankingTableHelper
import com.etologic.mahjongscoring3.domain.model.ShareGameOptions
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.utils.prettifyOneLine
import com.etologic.mahjongscoring3.domain.utils.prettifyTwoLines
import com.etologic.mahjongscoring3.domain.utils.toStringSigned
import com.etologic.mahjongscoring3.platform.LocalAppLocale
import com.etologic.mahjongscoring3.platform.PlatformActions
import com.etologic.mahjongscoring3.platform.rememberJsonFilePicker
import com.etologic.mahjongscoring3.platform.supportedLanguages
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.are_you_sure
import com.etologic.mahjongscoring3.resources.arrow
import com.etologic.mahjongscoring3.resources.best_hand
import com.etologic.mahjongscoring3.resources.chinese
import com.etologic.mahjongscoring3.resources.choose_language
import com.etologic.mahjongscoring3.resources.delete
import com.etologic.mahjongscoring3.resources.delete_game
import com.etologic.mahjongscoring3.resources.dutch
import com.etologic.mahjongscoring3.resources.english
import com.etologic.mahjongscoring3.resources.export_games
import com.etologic.mahjongscoring3.resources.french
import com.etologic.mahjongscoring3.resources.ic_add
import com.etologic.mahjongscoring3.resources.ic_bin
import com.etologic.mahjongscoring3.resources.ic_east
import com.etologic.mahjongscoring3.resources.ic_north
import com.etologic.mahjongscoring3.resources.ic_share
import com.etologic.mahjongscoring3.resources.ic_south
import com.etologic.mahjongscoring3.resources.ic_trophy_golden
import com.etologic.mahjongscoring3.resources.ic_west
import com.etologic.mahjongscoring3.resources.import_games
import com.etologic.mahjongscoring3.resources.importable
import com.etologic.mahjongscoring3.resources.italian
import com.etologic.mahjongscoring3.resources.language
import com.etologic.mahjongscoring3.resources.old_games
import com.etologic.mahjongscoring3.resources.plain_text
import com.etologic.mahjongscoring3.resources.player_four
import com.etologic.mahjongscoring3.resources.player_one
import com.etologic.mahjongscoring3.resources.player_three
import com.etologic.mahjongscoring3.resources.player_two
import com.etologic.mahjongscoring3.resources.rounds
import com.etologic.mahjongscoring3.resources.share
import com.etologic.mahjongscoring3.resources.share_files_using
import com.etologic.mahjongscoring3.resources.share_game
import com.etologic.mahjongscoring3.resources.share_game_using
import com.etologic.mahjongscoring3.resources.spanish
import com.etologic.mahjongscoring3.resources.start
import com.etologic.mahjongscoring3.resources.start_new_game
import com.etologic.mahjongscoring3.resources.table_csv
import com.etologic.mahjongscoring3.resources.there_arent_games_yet_start_one
import com.etologic.mahjongscoring3.ui.common.ArrowHeight
import com.etologic.mahjongscoring3.ui.common.ArrowWidth
import com.etologic.mahjongscoring3.ui.common.ConfirmDialog
import com.etologic.mahjongscoring3.ui.common.EmptyState
import com.etologic.mahjongscoring3.ui.common.ErrorDialog
import com.etologic.mahjongscoring3.ui.common.GameNames
import com.etologic.mahjongscoring3.ui.common.MmTopBar
import com.etologic.mahjongscoring3.ui.common.NamesDialog
import com.etologic.mahjongscoring3.ui.common.OverflowItem
import com.etologic.mahjongscoring3.ui.common.OverflowMenu
import com.etologic.mahjongscoring3.ui.common.SingleChoiceDialog
import com.etologic.mahjongscoring3.ui.common.TopBarNavigation
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.etologic.mahjongscoring3.ui.common.rememberSecureClick

@Composable
fun OldGamesScreen(
    viewModel: OldGamesViewModel,
    platformActions: PlatformActions,
    currentLanguage: String?,
    onOpenDrawer: () -> Unit,
    onChangeLanguage: (String) -> Unit,
    onOpenGame: (GameId) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()

    // Saveable: like the MS2 dialog fragment, the dialog stays open after an activity recreation
    var showSetupDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<GameId?>(null) }
    var gameToShare by remember { mutableStateOf<GameId?>(null) }

    val shareGameUsing = stringResource(Res.string.share_game_using)
    val shareFilesUsing = stringResource(Res.string.share_files_using)
    val pickFile = rememberJsonFilePicker { text -> text?.let(viewModel::importGames) }

    Column(Modifier.fillMaxSize()) {
        MmTopBar(
            title = stringResource(Res.string.old_games),
            navigation = TopBarNavigation.DRAWER,
            onNavigationClick = onOpenDrawer,
            actions = {
                OverflowMenu(
                    listOf(
                        OverflowItem(stringResource(Res.string.language), "action_language") { showLanguageDialog = true },
                        OverflowItem(stringResource(Res.string.export_games), "action_export_games") {
                            viewModel.exportGames { platformActions.shareFile(it, shareFilesUsing) }
                        },
                        OverflowItem(stringResource(Res.string.import_games), "action_import_games") { pickFile() },
                    )
                )
            },
        )
        Box(Modifier.fillMaxSize().background(MmColors.GrayLight)) {
            when (val state = uiState) {
                OldGamesUiState.Loading -> {}
                is OldGamesUiState.Loaded ->
                    if (state.oldGamesList.isEmpty()) {
                        EmptyState(
                            message = stringResource(Res.string.there_arent_games_yet_start_one),
                            modifier = Modifier.testTag("emptyLayoutOldGames"),
                            centerVertically = true,
                            footer = {
                                Image(
                                    painter = painterResource(Res.drawable.arrow),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(MmColors.Gray),
                                    modifier = Modifier.padding(
                                        start = MmDimens.TripleMargin,
                                        top = MmDimens.DoubleMargin,
                                        end = MmDimens.DoubleMargin,
                                        bottom = MmDimens.DoubleMargin,
                                    ).size(ArrowWidth, ArrowHeight),
                                )
                            },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().background(MmColors.Gray),
                            contentPadding = PaddingValues(bottom = 88.dp),
                        ) {
                            items(state.oldGamesList, key = { it.gameId }) { game ->
                                OldGameItem(
                                    uiGame = game,
                                    onClick = { onOpenGame(game.gameId) },
                                    onDeleteClick = { gameToDelete = game.gameId },
                                    onShareClick = { gameToShare = game.gameId },
                                )
                            }
                        }
                    }
            }
            FloatingActionButton(
                onClick = rememberSecureClick { showSetupDialog = true },
                backgroundColor = MmColors.Accent,
                contentColor = MmColors.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(MmDimens.StandardMargin).testTag("fabOldGames"),
            ) {
                Icon(painterResource(Res.drawable.ic_add), contentDescription = stringResource(Res.string.start_new_game))
            }
        }
    }

    if (showSetupDialog) {
        val defaultNames = defaultPlayerNames()
        NamesDialog(
            initial = GameNames(
                gameName = "",
                nameP1 = stringResource(Res.string.player_one),
                nameP2 = stringResource(Res.string.player_two),
                nameP3 = stringResource(Res.string.player_three),
                nameP4 = stringResource(Res.string.player_four),
            ),
            gameNameHint = null, // MS2 only had a design-time (tools:hint) date
            confirmText = stringResource(Res.string.start),
            onConfirm = { names ->
                viewModel.createGame(names.withDefaultPlayerNames(defaultNames)) { gameId ->
                    showSetupDialog = false
                    onOpenGame(gameId)
                }
            },
            onDismiss = { showSetupDialog = false },
        )
    }

    gameToDelete?.let { gameId ->
        ConfirmDialog(
            title = stringResource(Res.string.delete_game),
            message = stringResource(Res.string.are_you_sure),
            onConfirm = { viewModel.deleteGame(gameId) },
            onDismiss = { gameToDelete = null },
        )
    }

    gameToShare?.let { gameId ->
        ShareGameDialog(
            onOptionSelected = { option ->
                viewModel.shareGame(
                    gameId = gameId,
                    option = option,
                    showShareText = { platformActions.shareText(it, shareGameUsing) },
                    showShareFile = { platformActions.shareFile(it, shareFilesUsing) },
                )
            },
            onDismiss = { gameToShare = null },
        )
    }

    if (showLanguageDialog) {
        LanguageDialog(
            currentLanguage = currentLanguage ?: LocalAppLocale.current.take(2),
            onLanguageSelected = onChangeLanguage,
            onDismiss = { showLanguageDialog = false },
        )
    }

    error?.let { ErrorDialog(it, viewModel::dismissError) }
}

@Composable
internal fun defaultPlayerNames() = listOf(
    stringResource(Res.string.player_one),
    stringResource(Res.string.player_two),
    stringResource(Res.string.player_three),
    stringResource(Res.string.player_four),
)

fun GameNames.withDefaultPlayerNames(defaults: List<String>) = copy(
    nameP1 = nameP1.ifBlank { defaults[0] },
    nameP2 = nameP2.ifBlank { defaults[1] },
    nameP3 = nameP3.ifBlank { defaults[2] },
    nameP4 = nameP4.ifBlank { defaults[3] },
)

@Composable
fun ShareGameDialog(
    onOptionSelected: (ShareGameOptions) -> Unit,
    onDismiss: () -> Unit,
) {
    SingleChoiceDialog(
        title = stringResource(Res.string.share_game),
        items = listOf(
            stringResource(Res.string.plain_text),
            stringResource(Res.string.table_csv),
            stringResource(Res.string.importable),
        ),
        selectedIndex = -1,
        onSelected = { index -> onOptionSelected(ShareGameOptions.fromIndex(index)) },
        onDismiss = onDismiss,
    )
}

@Composable
private fun LanguageDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    SingleChoiceDialog(
        title = stringResource(Res.string.choose_language),
        items = listOf(
            stringResource(Res.string.chinese),
            stringResource(Res.string.dutch),
            stringResource(Res.string.english),
            stringResource(Res.string.french),
            stringResource(Res.string.spanish),
            stringResource(Res.string.italian),
        ),
        selectedIndex = supportedLanguages.indexOf(currentLanguage).takeIf { it >= 0 } ?: 2,
        onSelected = { index -> onLanguageSelected(supportedLanguages[index]) },
        onDismiss = onDismiss,
    )
}

@Composable
private fun OldGameItem(
    uiGame: UiGame,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    val bestHand = remember(uiGame) { uiGame.getBestHand() }
    val winnerName = remember(uiGame) { RankingTableHelper.generateRankingTable(uiGame)?.sortedPlayersRankings?.firstOrNull()?.name }
    val totals = uiGame.ongoingRound

    Card(
        modifier = Modifier.fillMaxWidth().padding(MmDimens.HalfMargin).testTag("cvOldGameItem").clickable(onClick = rememberSecureClick(action = onClick)),
        backgroundColor = MmColors.GrayLight,
        contentColor = MmColors.GrayMM,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(MmDimens.CardCornerRadius),
    ) {
        Column {
            // TITLE BAR
            Row(
                modifier = Modifier.fillMaxWidth().background(MmColors.Primary),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = uiGame.gameName.ifBlank { uiGame.startDate.prettifyOneLine() },
                    color = MmColors.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = MmDimens.StandardMargin, top = MmDimens.HalfMargin, bottom = MmDimens.HalfMargin),
                )
                IconButton(onClick = rememberSecureClick(action = onDeleteClick), modifier = Modifier.testTag("btOldGameItemDelete")) {
                    Icon(painterResource(Res.drawable.ic_bin), contentDescription = stringResource(Res.string.delete), tint = MmColors.White)
                }
                IconButton(onClick = rememberSecureClick(action = onShareClick), modifier = Modifier.padding(end = MmDimens.HalfMargin).testTag("btOldGameItemShare")) {
                    Icon(painterResource(Res.drawable.ic_share), contentDescription = stringResource(Res.string.share), tint = MmColors.White)
                }
            }
            // BODY
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                // SEATS
                Column(Modifier.weight(2f).padding(MmDimens.StandardMargin)) {
                    OldGameSeat(Res.drawable.ic_west, uiGame.nameP3, totals.totalPointsP3, winnerName == uiGame.nameP3, Modifier.align(Alignment.CenterHorizontally).width(100.dp))
                    Row(Modifier.fillMaxWidth()) {
                        OldGameSeat(Res.drawable.ic_north, uiGame.nameP4, totals.totalPointsP4, winnerName == uiGame.nameP4, Modifier.weight(1f).padding(end = MmDimens.HalfMargin))
                        OldGameSeat(Res.drawable.ic_south, uiGame.nameP2, totals.totalPointsP2, winnerName == uiGame.nameP2, Modifier.weight(1f))
                    }
                    OldGameSeat(Res.drawable.ic_east, uiGame.nameP1, totals.totalPointsP1, winnerName == uiGame.nameP1, Modifier.align(Alignment.CenterHorizontally).width(100.dp))
                }
                // ROUNDS & BEST HAND
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(top = MmDimens.QuarterMargin, bottom = MmDimens.StandardMargin),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(uiGame.startDate.prettifyTwoLines(), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Column(
                        modifier = Modifier.weight(1f).padding(top = MmDimens.StandardMargin),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(stringResource(Res.string.rounds), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(uiGame.finishedRounds.size.toString(), textAlign = TextAlign.Center)
                    }
                    Column(
                        modifier = Modifier.padding(top = MmDimens.StandardMargin),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(stringResource(Res.string.best_hand), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(bestHand.playerName.ifEmpty { "-" }, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                        Text(if (bestHand.handValue == 0) "-" else bestHand.handValue.toString(), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun OldGameSeat(
    windIcon: DrawableResource,
    name: String,
    points: Int,
    isWinner: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(windIcon), contentDescription = null, modifier = Modifier.size(MmDimens.StandardMargin))
        Text(name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(points.toStringSigned(), textAlign = TextAlign.Center)
            if (isWinner) {
                Spacer(Modifier.width(4.dp))
                Image(painterResource(Res.drawable.ic_trophy_golden), contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

