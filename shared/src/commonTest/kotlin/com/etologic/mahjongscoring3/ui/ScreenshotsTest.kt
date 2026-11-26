package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.testutil.GameScenario
import com.etologic.mahjongscoring3.testutil.GameScenarios
import kotlin.test.Test

/**
 * Port of MS2 ScreenshotsTest: the same 47 screens and dialogs, with the same data and device size,
 * written to shared/build/screenshots/android and shared/build/screenshots/ios with MS2 file names,
 * so they can be compared side by side with MS2 app/src/test/screenshots.
 *
 * MS2 verified pixel by pixel against its own recordings; MS3 renders with a different toolkit
 * (Compose instead of Views), so here the images are produced for a visual comparison, not asserted.
 */
@OptIn(ExperimentalTestApi::class)
class ScreenshotsTest : UiTestBase() {

    private fun scenarioTest(
        scenario: GameScenario,
        seatsArrangement: SeatsArrangement? = null,
        isDiffsFeatureEnabled: Boolean? = null,
        block: ComposeUiTest.() -> Unit,
    ) = appTest(scenario, seatsArrangement = seatsArrangement, isDiffsFeatureEnabled = isDiffsFeatureEnabled) {
        openGame(scenario)
        block()
    }

    private fun ComposeUiTest.openListTab() {
        click("tabGameLIST")
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
    }

    private fun ComposeUiTest.clickSeat(seat: String) {
        click(seat)
        awaitView("btHandActionsDialogHu")
    }

    private fun ComposeUiTest.withDiffsPressed(block: () -> Unit) {
        view("btTableSeatsShowDiffs").performTouchInput { down(center) }
        waitForIdle()
        block()
        view("btTableSeatsShowDiffs").performTouchInput { up() }
        waitForIdle()
    }

    // region Old games

    @Test
    fun oldGames_empty() = appTest { screenshot("old_games_empty") }

    @Test
    fun oldGames_list() = appTest(GameScenarios.fullGame, GameScenarios.penalties, GameScenarios.specialNames, GameScenarios.newGame) {
        awaitView("cvOldGameItem")
        screenshot("old_games_list")
    }

    @Test
    fun oldGames_newGameDialog() = appTest {
        click("fabOldGames")
        screenshot("old_games_new_game_dialog")
        click("btNamesDialogCancel")
    }

    @Test
    fun drawer_open() = appTest {
        openDrawer()
        screenshot("drawer_open")
    }

    // endregion

    // region Game table

    @Test
    fun gameTable_newGame() = scenarioTest(GameScenarios.newGame) { screenshot("game_table_new_game") }

    @Test
    fun gameTable_penalties_seatsDown() = scenarioTest(GameScenarios.penalties) { screenshot("game_table_penalties_down") }

    @Test
    fun gameTable_penalties_seatsOut() =
        scenarioTest(GameScenarios.penalties, seatsArrangement = SeatsArrangement.OUT) { screenshot("game_table_penalties_out") }

    @Test
    fun gameTable_penalties_seatsRanking() =
        scenarioTest(GameScenarios.penalties, seatsArrangement = SeatsArrangement.RANKING) { screenshot("game_table_penalties_ranking") }

    @Test
    fun gameTable_diffsShown() = scenarioTest(GameScenarios.penalties) {
        withDiffsPressed { screenshot("game_table_penalties_diffs") }
    }

    @Test
    fun gameTable_rankingWithDiffs() = scenarioTest(GameScenarios.randomGames[14], seatsArrangement = SeatsArrangement.RANKING) {
        withDiffsPressed { screenshot("game_table_ranking_diffs") }
    }

    @Test
    fun gameTable_diffsFeatureDisabled() =
        scenarioTest(GameScenarios.penalties, isDiffsFeatureEnabled = false) { screenshot("game_table_penalties_diffs_disabled") }

    @Test
    fun gameTable_southWind() = gameTableAtRound(GameScenarios.randomGames[4], "game_table_round_05_south")

    @Test
    fun gameTable_westWind() = gameTableAtRound(GameScenarios.randomGames[10], "game_table_round_11_west")

    @Test
    fun gameTable_northWind() = gameTableAtRound(GameScenarios.randomGames[14], "game_table_round_15_north")

    private fun gameTableAtRound(scenario: GameScenario, name: String) {
        check(scenario.game.endDate == null) { "${scenario.name} must be an ongoing game" }
        scenarioTest(scenario) { screenshot(name) }
    }

    @Test
    fun gameTable_endedGame() = scenarioTest(GameScenarios.fullGame) { screenshot("game_table_ended") }

    @Test
    fun gameTable_specialNames() = scenarioTest(GameScenarios.specialNames) { screenshot("game_table_special_names") }

    // endregion

    // region Game list

    @Test
    fun gameList_fullGame() = scenarioTest(GameScenarios.fullGame) {
        openListTab()
        screenshot("game_list_full_game")
    }

    @Test
    fun gameList_penalties() = scenarioTest(GameScenarios.penalties) {
        openListTab()
        screenshot("game_list_penalties")
    }

    // endregion

    // region Dialogs

    @Test
    fun dialog_handActions() = scenarioTest(GameScenarios.penalties) {
        clickSeat("iGameTableSeatSouth")
        screenshot("dialog_hand_actions")
    }

    @Test
    fun dialog_hu() = scenarioTest(GameScenarios.penalties) {
        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        screenshot("dialog_hu")
    }

    @Test
    fun dialog_hu_withPointsAndDiscarder() = scenarioTest(GameScenarios.penalties) {
        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        click("btCustomNumPad2", insideOf = "cnpGameHuDialog")
        click("btCustomNumPad4", insideOf = "cnpGameHuDialog")
        click("iDiscarderSelectorPlayer2Container")
        screenshot("dialog_hu_filled")
    }

    @Test
    fun dialog_penalty() = scenarioTest(GameScenarios.penalties) {
        clickSeat("iGameTableSeatWest")
        click("btHandActionsDialogPenalty")
        screenshot("dialog_penalty")
    }

    @Test
    fun dialog_ranking() = scenarioTest(GameScenarios.fullGame) {
        click("fabGameTable")
        awaitView("btRankingDialogClose")
        screenshot("dialog_ranking")
    }

    @Test
    fun dialog_editNames() = scenarioTest(GameScenarios.fullGame) {
        openMenuItem("action_edit_names")
        awaitView("btNamesDialogCancel")
        screenshot("dialog_edit_names")
        click("btNamesDialogCancel")
    }

    // endregion

    // region Other screens

    @Test
    fun combinations() = appTest {
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        screenshot("combinations")
    }

    @Test
    fun combinations_filtered() = appTest {
        click("bottomNavigation_combinations")
        awaitView("action_search")
        click("action_search")
        view("etCombinationsSearch").performTextReplacement("dragon")
        screenshot("combinations_filtered")
    }

    @Test
    fun diffsCalculator() = appTest {
        openDrawer()
        click("navigationView_diffsCalculator")
        awaitView("diffsCalculatorFragment")
        screenshot("diffs_calculator")
    }

    // endregion

    // region Languages

    @Test
    fun language_es() = languageSet("es")

    @Test
    fun language_fr() = languageSet("fr")

    @Test
    fun language_it() = languageSet("it")

    @Test
    fun language_nl() = languageSet("nl")

    @Test
    fun language_zh() = languageSet("zh")

    private fun languageSet(language: String) = appTest(GameScenarios.fullGame, GameScenarios.penalties, language = language) {
        awaitView("cvOldGameItem")
        screenshot("lang_${language}_old_games")
        openGame(GameScenarios.penalties)
        screenshot("lang_${language}_game_table")
        openListTab()
        screenshot("lang_${language}_game_list")
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        screenshot("lang_${language}_combinations")
    }

    // endregion
}
