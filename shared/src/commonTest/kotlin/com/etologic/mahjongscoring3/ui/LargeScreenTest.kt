package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.unit.width
import com.etologic.mahjongscoring3.testutil.GameScenario
import com.etologic.mahjongscoring3.testutil.GameScenarios
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Port of MS2 LargeScreenTest. Android 16 ignores the portrait lock on large screens (smallest width of 600dp or
 * more): tablets, unfolded foldables and desktop windows show the app in landscape and resize it freely.
 * These screenshots show every main screen on a 10" tablet in landscape, and the last test checks that the state
 * survives a rotation, which recreates the activity. iOS runs the same tests on an iPad sized window.
 */
@OptIn(ExperimentalTestApi::class)
class LargeScreenTest : UiTestBase() {

    private fun tabletTest(vararg seed: GameScenario, restorable: Boolean = false, block: ComposeUiTest.() -> Unit) =
        appTest(*seed, restorable = restorable, screen = TestScreen.TABLET_LANDSCAPE, block = block)

    private fun ComposeUiTest.openHuDialog() {
        openGame(GameScenarios.penalties)
        click("iGameTableSeatSouth")
        awaitView("btHandActionsDialogHu")
        click("btHandActionsDialogHu")
        awaitView("cnpGameHuDialog")
    }

    @Test
    fun oldGamesList() = tabletTest(GameScenarios.fullGame, GameScenarios.penalties, GameScenarios.specialNames, GameScenarios.newGame) {
        awaitView("cvOldGameItem")
        screenshot("tablet_land_old_games_list")
    }

    @Test
    fun drawer() = tabletTest {
        openDrawer()
        screenshot("tablet_land_drawer")
    }

    @Test
    fun gameTable() = tabletTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        screenshot("tablet_land_game_table")
    }

    @Test
    fun gameList() = tabletTest(GameScenarios.fullGame) {
        openGame(GameScenarios.fullGame)
        click("tabGameLIST")
        screenshot("tablet_land_game_list")
    }

    @Test
    fun handActionsDialog() = tabletTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("iGameTableSeatSouth")
        awaitView("btHandActionsDialogHu")
        screenshot("tablet_land_dialog_hand_actions")
    }

    @Test
    fun huDialog() = tabletTest(GameScenarios.penalties) {
        openHuDialog()
        screenshot("tablet_land_dialog_hu")
    }

    @Test
    fun rankingDialog() = tabletTest(GameScenarios.fullGame) {
        openGame(GameScenarios.fullGame)
        click("fabGameTable")
        awaitView("btRankingDialogClose")
        screenshot("tablet_land_dialog_ranking")
    }

    @Test
    fun combinations() = tabletTest {
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        screenshot("tablet_land_combinations")
    }

    @Test
    fun diffsCalculator() = tabletTest {
        openDrawer()
        click("navigationView_diffsCalculator")
        awaitView("diffsCalculatorFragment")
        screenshot("tablet_land_diffs_calculator")
    }

    @Test
    fun rotatingToPortraitKeepsTheOpenGameAndTheHuDialog() = tabletTest(GameScenarios.penalties, restorable = true) {
        openHuDialog()

        rotate(TestScreen.TABLET_PORTRAIT) {
            // The game is still the open one under the dialog, and the window is in portrait (it fills its width)
            awaitView("gameFragment")
            assertEquals(800f, view("gameFragment").getBoundsInRoot().width.value, absoluteTolerance = 1f)
            awaitView("btCustomNumPad2")
            view("btCustomNumPad2", insideOf = "cnpGameHuDialog").assertIsDisplayed()
            screenshot("tablet_port_dialog_hu_after_rotation")
        }
    }
}
