package com.etologic.mahjongscoring3.ms2

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasAnyAncestor
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.ui.UiTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Port of the MS2 instrumented crash tests (app/src/androidTest/.../app/crashes), which reproduce
 * Crashlytics issues. MS2 had Fragment/binding lifecycle problems that Compose does not have, so each
 * test here checks the same user situation on the MS3 screens instead of the MS2 internals:
 *
 * - DialogsWithoutLoadedGameCrashTest: dialogs open while the game is not loaded (here: the game
 *   disappears from the database while a dialog is open, and the user goes on using the dialog).
 * - HandActionsWithoutSelectedSeatCrashTest: the hand actions dialog after its seat is unselected.
 * - DiceDialogDismissCrashTest: the game goes away while the dice are rolling.
 * - GameListHighlightCrashTest: the list highlights the last round when it is far down the list.
 *
 * Not ported: DialogBindingOnDestroyCrashTest and DialogLifecycleTest (activity recreated with a dialog
 * open). They need an Activity recreation or StateRestorationTester, which the multiplatform
 * ComposeUiTest does not have.
 */
@OptIn(ExperimentalTestApi::class)
class CrashScenariosTest : UiTestBase() {

    private fun ComposeUiTest.clickSeat(seat: String) {
        click(seat)
        awaitView("btHandActionsDialogHu")
    }

    private fun ComposeUiTest.deleteGameInTheDatabase(gameId: Long) {
        runBlocking {
            rounds.deleteGameRounds(gameId).getOrThrow()
            games.deleteOne(gameId).getOrThrow()
        }
        awaitData { allGames().none { it.gameId == gameId } }
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
    }

    private fun assertNoRoundsOf(gameId: Long) {
        val orphans = runBlocking { rounds.getAllFlow().first() }.filter { it.gameId == gameId }
        assertTrue(orphans.isEmpty(), "Rounds saved for the deleted game $gameId: $orphans")
    }

    // region DialogsWithoutLoadedGameCrashTest

    @Test
    fun handActionsDialog_gameDeletedWhileOpen_doesNotCrash() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        clickSeat("iGameTableSeatEast")
        val gameId = GameScenarios.penalties.game.gameId
        deleteGameInTheDatabase(gameId)
        // MS3 keeps showing the last loaded game: saving a hand now must not crash nor leave orphan rounds
        click("btHandActionsDialogDraw")
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
        assertNoRoundsOf(gameId)
    }

    @Test
    fun huDialog_gameDeletedWhileOpen_doesNotCrash() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        clickSeat("iGameTableSeatEast")
        click("btHandActionsDialogHu")
        awaitView("btGameHuDialogOk")
        val gameId = GameScenarios.penalties.game.gameId
        deleteGameInTheDatabase(gameId)
        typePoints("cnpGameHuDialog", 8)
        click("btGameHuDialogOk")
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
        assertNoRoundsOf(gameId)
    }

    @Test
    fun diceDialog_gameDeletedWhileOpen_doesNotCrash() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("fabGameTable")
        awaitView("ivDice1")
        deleteGameInTheDatabase(GameScenarios.penalties.game.gameId)
        if (exists("ivDice1")) click("ivDice1")
        mainClock.advanceTimeBy(2_000)
        waitForIdle()
    }

    @Test
    fun editNamesDialog_gameDeletedWhileOpen_doesNotCrash() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        openMenuItem("action_edit_names")
        awaitView("btNamesDialogCancel")
        deleteGameInTheDatabase(GameScenarios.penalties.game.gameId)
        // The names dialog may stay open (it has its own copy of the names); it must not crash
        waitForIdle()
    }

    @Test
    fun rankingDialog_gameDeletedWhileOpen_doesNotCrash() = appTest(GameScenarios.sixteenDraws) {
        openGame(GameScenarios.sixteenDraws)
        // An ended game shows the ranking button instead of the dice
        click("fabGameTable")
        awaitView("btRankingDialogClose")
        deleteGameInTheDatabase(GameScenarios.sixteenDraws.game.gameId)
        if (exists("btRankingDialogClose")) click("btRankingDialogClose")
        waitForIdle()
    }

    // endregion

    // region HandActionsWithoutSelectedSeatCrashTest

    @Test
    fun huPage_closed_thenAnotherSeatOpensItsOwnDialog() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatEast")
        click("btHandActionsDialogHu")
        click("btGameHuDialogCancel")
        awaitData { !exists("btGameHuDialogCancel") && !exists("btHandActionsDialogHu") }

        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        awaitView("cdsGameHuDialog")
        // The new dialog is for South (Player B), so Player B is not a possible discarder
        val southName = GameScenarios.newGame.game.nameP2
        assertTrue(
            onAllNodes(hasText(southName) and hasAnyAncestor(hasTestTag("cdsGameHuDialog")), useUnmergedTree = true)
                .fetchSemanticsNodes().isEmpty(),
            "The discarders of South's hu dialog must not include South ($southName)",
        )
    }

    // endregion

    // region DiceDialogDismissCrashTest

    @Test
    fun diceDialog_closedWhileRolling_doesNotCrash() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("fabGameTable")
        click("ivDice1")
        // Close it before the roll finishes (the game disappears, so the screen removes the dialog)
        deleteGameInTheDatabase(GameScenarios.penalties.game.gameId)
        mainClock.advanceTimeBy(2_000)
        waitForIdle()
    }

    // endregion

    // region GameListHighlightCrashTest

    @Test
    fun gameList_highlightLastRound_afterManyRounds_showsIt() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        val seats = listOf("iGameTableSeatEast", "iGameTableSeatSouth", "iGameTableSeatWest", "iGameTableSeatNorth")
        repeat(11) { index ->
            click("tabGameTABLE")
            clickSeat(seats[index % 4])
            click("btHandActionsDialogDraw")
            awaitData { storedRounds(GameScenarios.newGame.game.gameId).size == index + 2 }
        }
        // After saving, MS3 shows the list and scrolls to the last round (11 draws + the ongoing round 12)
        awaitData { exists("tabGameLIST") }
        mainClock.advanceTimeBy(2_000)
        waitForIdle()
        view(hasTestTag("tvGameListItemRoundNumber") and hasText("11")).assertIsDisplayed()
    }

    // endregion
}
