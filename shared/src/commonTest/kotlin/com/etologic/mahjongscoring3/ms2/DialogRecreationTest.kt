package com.etologic.mahjongscoring3.ms2

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performTextReplacement
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.ui.UiTestBase
import com.etologic.mahjongscoring3.ui.pressBack
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Port of the MS2 tests where the activity is recreated (rotation, "Don't keep activities") with a
 * dialog open: DialogLifecycleTest, DialogBindingOnDestroyCrashTest and the recreation tests of
 * UiEdgeCasesTest (MS2 fix 38a896f: the hand actions dialog keeps its seat).
 *
 * MS2 crashed there before its fixes. After them, the dialog and what the user typed are still there
 * after the recreation, so MS3 must do the same. [recreate] rebuilds the content from its saved state
 * and keeps the ViewModels, like an activity recreation.
 */
@OptIn(ExperimentalTestApi::class)
class DialogRecreationTest : UiTestBase() {

    private val penalties = GameScenarios.penalties

    private fun storedPenaltiesRounds() = storedRounds(penalties.game.gameId)

    // region Names dialogs (MS2 DialogLifecycleTest, DialogBindingOnDestroyCrashTest)

    @Test
    fun newGameDialog_survivesActivityRecreation() = appTest(restorable = true) {
        click("fabOldGames")
        awaitView("tietNamesDialogEast")
        view("tietNamesDialogEast").performTextReplacement("Pepe")

        recreate()

        awaitView("btNamesDialogCancel")
        view("btNamesDialogCancel").assertIsDisplayed()
        view("tietNamesDialogEast").assertTextEquals("Pepe")
    }

    @Test
    fun editNamesDialog_survivesActivityRecreation() = appTest(penalties, restorable = true) {
        openGame(penalties)
        openMenuItem("action_edit_names")
        awaitView("tietNamesDialogEast")
        view("tietNamesDialogEast").performTextReplacement("Pepe")

        recreate()

        awaitView("btNamesDialogCancel")
        view("btNamesDialogCancel").assertIsDisplayed()
        view("tietNamesDialogEast").assertTextEquals("Pepe")
    }

    // endregion

    // region Hand actions dialog (MS2 UiEdgeCasesTest)

    @Test
    fun recreate_withTheHandActionsDialogOpen_keepsItOpen() = appTest(penalties, restorable = true) {
        openGame(penalties)
        val before = storedPenaltiesRounds()
        click("iGameTableSeatWest")
        awaitView("btHandActionsDialogDraw")

        recreate()

        awaitView("btHandActionsDialogDraw")
        assertEquals(before, storedPenaltiesRounds())
        // The restored dialog still works for the selected seat
        click("btHandActionsDialogDraw")
        awaitData { storedPenaltiesRounds().size == before.size + 1 }
        awaitData { !exists("btHandActionsDialogDraw") }
    }

    @Test
    fun recreate_withTheHandActionsDialogOpen_thenBack_closesIt() = appTest(penalties, restorable = true) {
        openGame(penalties)
        click("iGameTableSeatWest")
        awaitView("btHandActionsDialogDraw")

        recreate()

        awaitView("btHandActionsDialogDraw")
        pressBack()
        awaitData { !exists("btHandActionsDialogDraw") }
        click("iGameTableSeatEast")
        awaitView("btHandActionsDialogDraw")
    }

    // endregion
}
