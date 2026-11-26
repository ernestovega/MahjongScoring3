package com.etologic.mahjongscoring3.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import com.etologic.mahjongscoring3.testutil.GameScenarios
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText

/**
 * Port of MS2 EdgeToEdgeTest. Apps targeting Android 15+ are always drawn edge-to-edge, so the app applies the
 * system bar, cutout and keyboard insets itself (App and AppInsets). These tests send fake insets and check where
 * the app bar and the bottom navigation end up, by their bounds.
 *
 * MS2 sent 63, 42, 700, 126 and 80px on its 420dpi screen; here they are in dp (24, 16, 266, 48 and 30dp).
 * MS2 read the paddings of its views. In Compose a padding is not a property of a node, so the tests check the
 * positions it gives: the app bar starts below the status bar, the bottom navigation background goes down to the
 * screen edge, and its items end above the navigation bar.
 */
@OptIn(ExperimentalTestApi::class)
class EdgeToEdgeTest : UiTestBase() {

    private val statusBar = 24.dp
    private val navigationBar = 16.dp
    private val keyboard = 266.dp
    private val sideNavigationBar = 48.dp
    private val cutout = 30.dp

    private fun ComposeUiTest.sendBars(top: Dp = statusBar, bottom: Dp = navigationBar, ime: Dp = 0.dp) =
        sendInsets(systemBars = WindowInsets(top = top, bottom = bottom), ime = WindowInsets(bottom = ime))

    private fun ComposeUiTest.sendLandscapeNotch() = sendInsets(
        systemBars = WindowInsets(top = statusBar, right = sideNavigationBar),
        displayCutout = WindowInsets(left = cutout),
    )

    private data class Layout(val screen: DpRect, val appBar: DpRect, val bottomNavigation: DpRect, val bottomItem: DpRect)

    /** The screen comes from [TestScreen]: on iOS the root node has the size of the test surface. */
    private fun ComposeUiTest.layout(screen: TestScreen = TestScreen.PHONE): Layout {
        waitForIdle()
        return Layout(
            screen = DpRect(0.dp, 0.dp, screen.widthDp.dp, screen.heightDp.dp),
            appBar = view("appBarLayout").getBoundsInRoot(),
            bottomNavigation = view("bottomNavigationView").getBoundsInRoot(),
            bottomItem = view("bottomNavigation_oldGames").getBoundsInRoot(),
        )
    }

    /** Px rounding: 266dp is 698.25px at 420dpi. */
    private fun assertDp(expected: Dp, actual: Dp, what: String) =
        assertEquals(expected.value, actual.value, absoluteTolerance = 1f, message = what)

    private fun assertBars(layout: Layout, top: Dp, navigationBar: Dp, keyboard: Dp = 0.dp) = with(layout) {
        assertDp(top, appBar.top, "app bar top")
        assertDp(screen.left, appBar.left, "app bar left")
        assertDp(screen.right, appBar.right, "app bar right")
        assertDp(screen.bottom - keyboard, bottomNavigation.bottom, "bottom navigation bottom")
        assertDp(bottomNavigation.bottom - navigationBar, bottomItem.bottom, "bottom navigation items bottom")
    }

    @Test
    fun statusBarPadsTheAppBar_andNavigationBarPadsTheBottomNavigation() = appTest {
        awaitView("appBarLayout")
        val before = layout()
        assertBars(before, top = 0.dp, navigationBar = 0.dp)

        sendBars()

        val after = layout()
        assertBars(after, top = statusBar, navigationBar = navigationBar)
        assertDp(before.bottomNavigation.height + navigationBar, after.bottomNavigation.height, "bottom navigation height")
    }

    @Test
    fun insetsAreReplaced_notAccumulated_whenTheyChange() = appTest {
        awaitView("appBarLayout")
        sendBars()
        sendBars()
        sendBars(top = 30.dp, bottom = 10.dp)
        assertBars(layout(), top = 30.dp, navigationBar = 10.dp)
    }

    @Test
    fun keyboardLiftsTheWholeContentAboveIt() = appTest {
        awaitView("appBarLayout")
        sendBars(ime = keyboard)
        // The keyboard covers the navigation bar, so the bottom navigation loses its navigation bar padding
        assertBars(layout(), top = statusBar, navigationBar = 0.dp, keyboard = keyboard)

        sendBars()

        assertBars(layout(), top = statusBar, navigationBar = navigationBar)
    }

    @Test
    fun sideBarsAndDisplayCutoutsInLandscapePadTheContentSides() = appTest {
        awaitView("appBarLayout")
        sendLandscapeNotch()
        with(layout()) {
            assertDp(cutout, appBar.left, "app bar left")
            assertDp(screen.right - sideNavigationBar, appBar.right, "app bar right")
            assertDp(cutout, bottomNavigation.left, "bottom navigation left")
            assertDp(screen.right - sideNavigationBar, bottomNavigation.right, "bottom navigation right")
            assertDp(screen.bottom, bottomNavigation.bottom, "bottom navigation bottom")
        }
    }

    @Test
    fun screenshotWithSystemBars() = appTest {
        awaitView("emptyLayoutOldGames")
        sendBars()
        screenshot("edge_to_edge_old_games")
    }

    @Test
    fun screenshotOfAGameWithSystemBars() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        sendBars()
        screenshot("edge_to_edge_game_table")
    }

    @Test
    fun screenshotOfTheDrawerWithSystemBars() = appTest {
        sendBars()
        openDrawer()
        screenshot("edge_to_edge_drawer")
    }

    @Test
    fun screenshotOfTheFullScreenHandActionsDialog() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        sendBars()
        click("iGameTableSeatSouth")
        awaitView("btHandActionsDialogHu")
        screenshot("edge_to_edge_dialog_hand_actions")
    }

    @Test
    fun screenshotWithTheKeyboardOpen() = appTest {
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        sendBars(ime = keyboard)
        screenshot("edge_to_edge_keyboard_open_combinations")
    }

    @Test
    fun screenshotInLandscapeWithANotchAndSideNavigationBar() = appTest(GameScenarios.penalties, screen = TestScreen.PHONE_LANDSCAPE) {
        openGame(GameScenarios.penalties)
        sendLandscapeNotch()
        screenshot("edge_to_edge_landscape_notch_game_table")
    }

    /**
     * Change from MS2: its landscape phone table only shows the wind icon of the West and East players.
     * MS3 shows the four players, inside the table (below the tabs and above the bottom navigation).
     */
    @Test
    fun landscapePhoneTableShowsTheFourPlayers() = appTest(GameScenarios.penalties, screen = TestScreen.PHONE_LANDSCAPE) {
        openGame(GameScenarios.penalties)
        sendLandscapeNotch()
        val table = view("iTableSeatsBySeat").getBoundsInRoot()
        listOf("Ana", "Bruno", "Carla", "David").forEach { name ->
            val node = view(hasTestTag("tvTableSeatBySeatName") and hasText(name))
            node.assertIsDisplayed()
            val bounds = node.getBoundsInRoot()
            assertTrue(bounds.height > 20.dp, "$name has room for its text: $bounds")
            assertTrue(bounds.top >= table.top - 1.dp && bounds.bottom <= table.bottom + 1.dp, "$name is inside the table: $bounds, $table")
        }
    }
}
