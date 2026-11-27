package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.dp
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.testutil.GameScenario
import com.etologic.mahjongscoring3.testutil.GameScenarios
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every screen, dialog and state of the app on the landscape screens where it really turns: Android 16 ignores the
 * portrait lock on large screens (7" and 10" tablets, unfolded foldables), and iPadOS shows the app in landscape
 * windows. Each test opens one state on each of [LANDSCAPE_LARGE_SCREENS], saves a screenshot
 * (land_<screen>_<state>.png) and fails if a text or a button is cut, a text does not fit, two texts cover each
 * other or a dialog is larger than the screen (see [layoutProblems]).
 */
@OptIn(ExperimentalTestApi::class)
class LandscapeLargeScreensTest : UiTestBase() {

    /** The longest names the names dialog accepts (MS2 maxLength: 60 for the game, 14 for each player). */
    private val longNames = GameScenarios.penalties.withLongNames()

    /** An ended game (ranking dialog) with the longest names. */
    private val longNamesEnded = GameScenarios.fullGame.withLongNames()

    private fun GameScenario.withLongNames() = copy(
        game = game.copy(
            gameName = "Campeonato de Asturias de Mahjong Competición Oficial 2026",
            nameP1 = "Bartolomé Ruiz",
            nameP2 = "Encarna Moreno",
            nameP3 = "Wenceslao Díaz",
            nameP4 = "Maximiliano MM",
        ),
    )

    private fun check(
        name: String,
        vararg seed: GameScenario,
        seatsArrangement: SeatsArrangement? = null,
        allowEllipsis: Boolean = false,
        onlyInside: String? = null,
        block: ComposeUiTest.(TestScreen) -> Unit,
    ) {
        val problems = mutableListOf<String>()
        LANDSCAPE_LARGE_SCREENS.forEach { screen ->
            appTest(*seed, seatsArrangement = seatsArrangement, screen = screen) {
                block(screen)
                screenshot("land_${screen.name.lowercase()}_$name")
                problems += layoutProblems(screen, allowEllipsis, onlyInside).map { "${screen.name}: $it" }
            }
        }
        assertTrue(problems.isEmpty(), "Layout problems in $name:\n" + problems.joinToString("\n"))
    }

    private fun ComposeUiTest.openSeat(seat: String) {
        click(seat)
        awaitView("btHandActionsDialogHu")
    }

    /** The hand actions dialog scrolls on short screens, like in MS2: its last buttons can be below the screen. */
    private fun ComposeUiTest.clickScrolled(tag: String) {
        awaitView(tag)
        view(tag).performScrollTo().performClick()
        waitForIdle()
    }

    private fun ComposeUiTest.openList() {
        click("tabGameLIST")
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
    }

    // region Old games

    @Test
    fun oldGamesEmpty() = check("old_games_empty") { awaitView("emptyLayoutOldGames") }

    @Test
    fun oldGamesList() = check("old_games_list", GameScenarios.fullGame, GameScenarios.penalties, GameScenarios.specialNames, GameScenarios.newGame) {
        awaitView("cvOldGameItem")
    }

    @Test
    fun oldGamesLongNames() = check("old_games_long_names", longNames, GameScenarios.bigScores, allowEllipsis = true) {
        awaitView("cvOldGameItem")
    }

    @Test
    fun oldGamesOverflowMenu() = check("old_games_menu") {
        click("action_overflow")
        awaitView("action_language")
    }

    @Test
    fun languageDialog() = check("dialog_language") { openMenuItem("action_language") }

    @Test
    fun deleteGameDialog() = check("dialog_delete_game", GameScenarios.penalties) {
        click("btOldGameItemDelete")
        awaitView("btAlertDialogPositive")
    }

    @Test
    fun newGameDialog() = check("dialog_new_game") {
        click("fabOldGames")
        awaitView("btNamesDialogSave")
    }

    @Test
    fun editNamesDialogWithLongNames() = check("dialog_edit_names_long", longNames, allowEllipsis = true) {
        openGame(longNames)
        openMenuItem("action_edit_names")
        awaitView("btNamesDialogSave")
    }

    @Test
    fun drawer() = check("drawer", GameScenarios.penalties, onlyInside = "navigationView") { openDrawer() }

    // endregion

    // region Game table

    @Test
    fun tableNewGame() = check("table_new_game", GameScenarios.newGame) { openGame(GameScenarios.newGame) }

    @Test
    fun tableBySeat() = check("table_by_seat", GameScenarios.penalties) { openGame(GameScenarios.penalties) }

    @Test
    fun tableOut() = check("table_out", GameScenarios.penalties, seatsArrangement = SeatsArrangement.OUT) { openGame(GameScenarios.penalties) }

    @Test
    fun tableRanking() = check("table_ranking", GameScenarios.penalties, seatsArrangement = SeatsArrangement.RANKING) { openGame(GameScenarios.penalties) }

    @Test
    fun tableDiffs() = check("table_diffs", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        view("btTableSeatsShowDiffs").performTouchInput { down(center) }
        waitForIdle()
    }

    @Test
    fun tableRankingDiffs() = check("table_ranking_diffs", GameScenarios.penalties, seatsArrangement = SeatsArrangement.RANKING) {
        openGame(GameScenarios.penalties)
        view("btTableSeatsShowDiffs").performTouchInput { down(center) }
        waitForIdle()
    }

    @Test
    fun tableLongNames() = check("table_long_names", longNames, allowEllipsis = true) { openGame(longNames) }

    @Test
    fun tableBigScores() = check("table_big_scores", GameScenarios.bigScores) { openGame(GameScenarios.bigScores) }

    @Test
    fun tableEndedGame() = check("table_ended", GameScenarios.fullGame) { openGame(GameScenarios.fullGame) }

    @Test
    fun gameOverflowMenu() = check("game_menu", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("action_overflow")
        awaitView("action_edit_names")
    }

    // endregion

    // region Game list

    @Test
    fun listFullGame() = check("list_full_game", GameScenarios.fullGame) {
        openGame(GameScenarios.fullGame)
        openList()
    }

    @Test
    fun listPenalties() = check("list_penalties", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        openList()
    }

    @Test
    fun listBigScores() = check("list_big_scores", GameScenarios.bigScores) {
        openGame(GameScenarios.bigScores)
        openList()
    }

    @Test
    fun listLongNames() = check("list_long_names", longNames, allowEllipsis = true) {
        openGame(longNames)
        openList()
    }

    @Test
    fun listDeleteRoundMenu() = check("list_delete_round_menu", GameScenarios.selfPicks) {
        openGame(GameScenarios.selfPicks)
        openList()
        // Phones in landscape show only the last rounds: the list scrolls to the first one, like a user
        val firstRound = hasTestTag("llGameListItemContainer") and hasAnyDescendant(hasTestTag("tvGameListItemRoundNumber") and hasText("1"))
        onNode(hasScrollToNodeAction() and hasAnyDescendant(hasTestTag("llGameListItemContainer")) and !hasTestTag("viewPagerGame"), useUnmergedTree = true)
            .performScrollToNode(firstRound)
        view(firstRound).performClick()
        awaitView("action_delete_round")
    }

    // endregion

    // region Game dialogs

    @Test
    fun handActionsDialog() = check("dialog_hand_actions", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        openSeat("iGameTableSeatSouth")
    }

    @Test
    fun handActionsDialogLongNames() = check("dialog_hand_actions_long", longNames, allowEllipsis = true) {
        openGame(longNames)
        openSeat("iGameTableSeatSouth")
    }

    @Test
    fun huDialog() = check("dialog_hu", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        openSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        awaitView("cnpGameHuDialog")
    }

    @Test
    fun huDialogFilledWithLongNames() = check("dialog_hu_filled_long", longNames, allowEllipsis = true) {
        openGame(longNames)
        openSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        awaitView("cnpGameHuDialog")
        typePoints("cnpGameHuDialog", 88)
        click("iDiscarderSelectorPlayer2Container")
    }

    @Test
    fun penaltyDialog() = check("dialog_penalty", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        openSeat("iGameTableSeatWest")
        clickScrolled("btHandActionsDialogPenalty")
        awaitView("cnpPenaltyDialog")
        typePoints("cnpPenaltyDialog", 30)
    }

    @Test
    fun cancelPenaltiesConfirmation() = check("dialog_cancel_penalties", GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        openSeat("iGameTableSeatSouth")
        clickScrolled("btHandActionsDialogPenalty")
        typePoints("cnpPenaltyDialog", 30)
        click("btPenaltyDialogSave")
        openSeat("iGameTableSeatEast")
        clickScrolled("btHandActionsDialogPenaltiesCancel")
        awaitView("btAlertDialogPositive")
    }

    @Test
    fun diceDialog() = check("dialog_dice", GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("fabGameTable")
        waitForIdle()
    }

    @Test
    fun rankingDialog() = check("dialog_ranking", GameScenarios.fullGame) {
        openGame(GameScenarios.fullGame)
        click("fabGameTable")
        awaitView("btRankingDialogClose")
    }

    @Test
    fun rankingDialogLongNames() = check("dialog_ranking_long", longNamesEnded, allowEllipsis = true) {
        openGame(longNamesEnded)
        click("fabGameTable")
        awaitView("btRankingDialogClose")
    }

    @Test
    fun tableOutLongNames() = check("table_out_long_names", longNames, seatsArrangement = SeatsArrangement.OUT, allowEllipsis = true) {
        openGame(longNames)
    }

    // endregion

    // region Other screens

    @Test
    fun combinations() = check("combinations") {
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
    }

    /** The keyboard covers about half of a landscape screen. */
    @Test
    fun combinationsSearchWithKeyboard() = check("combinations_search_keyboard") { screen ->
        click("bottomNavigation_combinations")
        awaitView("action_search")
        click("action_search")
        view("etCombinationsSearch").performTextReplacement("dragon")
        sendInsets(systemBars = WindowInsets(0, 0, 0, 0), ime = WindowInsets(bottom = (screen.heightDp * 0.45f).dp))
    }

    @Test
    fun diffsCalculator() = check("diffs_calculator") {
        openDrawer()
        click("navigationView_diffsCalculator")
        awaitView("diffsCalculatorFragment")
    }

    // endregion
}
