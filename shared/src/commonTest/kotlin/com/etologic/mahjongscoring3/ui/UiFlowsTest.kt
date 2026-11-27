package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.NORTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.TableWinds.WEST
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.player_four
import com.etologic.mahjongscoring3.resources.player_one
import com.etologic.mahjongscoring3.resources.player_three
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.testutil.assertThat
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import kotlin.test.Test

/**
 * Port of MS2 UiFlowsTest: end-to-end user flows on the real UI against a real Room database.
 * Each flow asserts both what the user sees and what ends up persisted.
 *
 * MS2 destinations are checked through the tag of each screen root (oldGamesFragment, gameFragment...),
 * and MS2 dialog destinations through a view only that dialog has.
 */
@OptIn(ExperimentalTestApi::class)
class UiFlowsTest : UiTestBase() {

    // region Helpers

    private fun ComposeUiTest.seat(seat: String): SemanticsNodeInteraction {
        awaitView(seat) // the seats appear when the game is loaded, after the tabs
        return view("tvTableSeatBySeatName", insideOf = seat)
    }

    private fun ComposeUiTest.seatPoints(seat: String): SemanticsNodeInteraction {
        awaitView(seat)
        return view("tvTableSeatBySeatPoints", insideOf = seat)
    }

    private fun ComposeUiTest.clickSeat(seat: String) {
        click(seat)
        awaitView("btHandActionsDialogHu")
    }

    private fun ComposeUiTest.confirmAlertDialog() {
        awaitView("btAlertDialogPositive")
        click("btAlertDialogPositive")
    }

    private fun RoundRecord.areTherePenaltiesInDb() = penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0

    private fun string(resource: org.jetbrains.compose.resources.StringResource) = runBlocking { getString(resource) }

    private fun ComposeUiTest.awaitRounds(gameId: GameId, count: Int) = awaitData { storedRounds(gameId).size == count }

    // endregion

    // region Old games

    @Test
    fun createGame_fromOldGames_opensTheGameAndPersistsIt() = appTest {
        awaitView("emptyLayoutOldGames")
        view("emptyLayoutOldGames").assertIsDisplayed()
        click("fabOldGames")
        view("tietNamesDialogGameName").performTextReplacement("Liga")
        view("tietNamesDialogEast").performTextReplacement("Ana")
        view("tietNamesDialogSouth").performTextReplacement("Bruno")
        view("tietNamesDialogWest").performTextReplacement("   ")
        view("tietNamesDialogNorth").performTextReplacement("David")
        click("btNamesDialogSave")

        awaitView("gameFragment")
        awaitData { allGames().size == 1 && storedRounds(allGames().single().gameId).size == 1 }
        val game = allGames().single()
        assertThat(game.gameName).isEqualTo("Liga")
        // A blank name falls back to the default "Player 3" label.
        assertThat(listOf(game.nameP1, game.nameP2, game.nameP3, game.nameP4))
            .containsExactly("Ana", "Bruno", string(Res.string.player_three), "David").inOrder()
        assertThat(storedRounds(game.gameId)).hasSize(1)
        awaitView("tvGameTableGameName")
        view("tvGameTableGameName").assertTextEquals("Liga")
        seat("iGameTableSeatEast").assertTextEquals("Ana")
        seat("iGameTableSeatSouth").assertTextEquals("Bruno")
        seat("iGameTableSeatNorth").assertTextEquals("David")
    }

    @Test
    fun createGame_defaultValues() = appTest {
        click("fabOldGames")
        view("tietNamesDialogEast").assertTextEquals(string(Res.string.player_one), includeEditableText = true)
        view("tietNamesDialogNorth").assertTextEquals(string(Res.string.player_four), includeEditableText = true)
        click("btNamesDialogSave")
        awaitData { allGames().size == 1 }
        assertThat(allGames().single().gameName).matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
    }

    @Test
    fun cancelNewGame_createsNothing() = appTest {
        click("fabOldGames")
        click("btNamesDialogCancel")
        assertThat(allGames()).isEmpty()
        view("oldGamesFragment").assertIsDisplayed()
    }

    @Test
    fun oldGameCard_opensThatGame() = appTest(GameScenarios.penalties, GameScenarios.fullGame) {
        openGame(GameScenarios.penalties)
        view("gameFragment").assertIsDisplayed()
        view("tvGameTableGameName").assertTextEquals("penalties")
    }

    @Test
    fun deleteGame_afterConfirmation() = appTest(GameScenarios.penalties) {
        awaitView("btOldGameItemDelete")
        click("btOldGameItemDelete")
        assertThat(allGames()).hasSize(1)
        confirmAlertDialog()
        awaitData { allGames().isEmpty() }
        awaitView("emptyLayoutOldGames")
        view("emptyLayoutOldGames").assertIsDisplayed()
    }

    // endregion

    // region Navigation

    @Test
    fun bottomNavigation_gameIsEnabledOnlyWithAnActiveGame() = appTest(GameScenarios.penalties) {
        view("bottomNavigation_game").assertIsNotEnabled()
        openGame(GameScenarios.penalties)
        view("bottomNavigation_game").assertIsEnabled()

        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        click("bottomNavigation_game")
        awaitView("gameFragment")
        view("gameFragment").assertIsDisplayed()
        click("bottomNavigation_oldGames")
        awaitView("oldGamesFragment")
        view("oldGamesFragment").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_fromGameToOldGames() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("bottomNavigation_oldGames")
        awaitView("oldGamesFragment")
        view("oldGamesFragment").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_fromCombinationsToOldGames_afterVisitingGame() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        click("bottomNavigation_oldGames")
        awaitView("oldGamesFragment")
        view("oldGamesFragment").assertIsDisplayed()
        // And the game tab still opens the active game.
        click("bottomNavigation_game")
        awaitView("gameFragment")
        view("tvGameTableGameName").assertTextEquals("penalties")
    }

    @Test
    fun drawer_navigatesToDiffsCalculator() = appTest {
        openDrawer()
        click("navigationView_diffsCalculator")
        awaitView("diffsCalculatorFragment")
        view("diffsCalculatorFragment").assertIsDisplayed()
    }

    // endregion

    // region Hands

    @Test
    fun huByDiscard_isSavedWithInitialSeatsAndShowsTheList() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogHu")
        typePoints("cnpGameHuDialog", 24)
        click("iDiscarderSelectorPlayer2Container") // second loser for South winner = West
        click("btGameHuDialogOk")

        awaitRounds(1, 2)
        val stored = storedRounds(1)
        assertThat(stored).hasSize(2)
        assertThat(stored[0].winnerInitialSeat).isEqualTo(SOUTH)
        assertThat(stored[0].discarderInitialSeat).isEqualTo(WEST)
        assertThat(stored[0].handPoints).isEqualTo(24)
        assertThat(stored[1].isOngoing()).isTrue()
        // The list page is shown
        waitUntil(timeoutMillis = 5_000) { runCatching { view("tabGameLIST").assertIsSelected() }.isSuccess }
        view(hasText("Totals")).assertIsDisplayed()
    }

    @Test
    fun huBySelfPick_whenNoDiscarderIsSelected() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatNorth")
        click("btHandActionsDialogHu")
        typePoints("cnpGameHuDialog", 8)
        click("btGameHuDialogOk")
        awaitRounds(1, 2)
        val first = storedRounds(1).first()
        assertThat(first.winnerInitialSeat).isEqualTo(NORTH)
        assertThat(first.discarderInitialSeat).isEqualTo(NONE)
        assertThat(storedGame(1).ongoingRound.totalPointsP4).isEqualTo(48)
    }

    @Test
    fun hu_belowMinimumPoints_isRejected() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatEast")
        click("btHandActionsDialogHu")
        typePoints("cnpGameHuDialog", 7)
        click("btGameHuDialogOk")
        assertThat(storedRounds(1)).hasSize(1)
        assertThat(exists("btGameHuDialogOk")).isTrue()
    }

    @Test
    fun hu_inALaterWind_mapsCurrentSeatsToInitialSeats() = appTest(GameScenarios.penalties) {
        // Round 5 (South wind): the current East seat is the initial South player (Bruno).
        val gameId = GameScenarios.penalties.game.gameId
        openGame(GameScenarios.penalties)
        seat("iGameTableSeatEast").assertTextEquals("Bruno")
        clickSeat("iGameTableSeatEast")
        click("btHandActionsDialogHu")
        typePoints("cnpGameHuDialog", 10)
        click("btGameHuDialogOk")
        awaitRounds(gameId, 6)
        val stored = storedRounds(gameId)
        assertThat(stored[4].winnerInitialSeat).isEqualTo(SOUTH)
        assertThat(stored[4].discarderInitialSeat).isEqualTo(NONE)
    }

    @Test
    fun draw_isSaved() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatWest")
        click("btHandActionsDialogDraw")
        awaitRounds(1, 2)
        assertThat(storedRounds(1).map { it.winnerInitialSeat }).containsExactly(NONE, null).inOrder()
    }

    @Test
    fun penalty_dividedAndCancelled() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogPenalty")
        typePoints("cnpPenaltyDialog", 30)
        view("cbPenaltyDialog").assertIsOn() // "Divided" is checked by default
        click("btPenaltyDialogSave")
        awaitData { storedRounds(1).single().areTherePenaltiesInDb() }
        with(storedRounds(1).single()) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(10, -30, 10, 10).inOrder()
        }
        waitForIdle()
        seatPoints("iGameTableSeatSouth").assertTextEquals("-30")

        clickSeat("iGameTableSeatEast")
        click("btHandActionsDialogPenaltiesCancel")
        confirmAlertDialog()
        awaitData { !storedRounds(1).single().areTherePenaltiesInDb() }
        assertThat(storedRounds(1).single().areTherePenaltiesInDb()).isFalse()
    }

    @Test
    fun penalty_dividedMustBeMultipleOfThree() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        clickSeat("iGameTableSeatSouth")
        click("btHandActionsDialogPenalty")
        typePoints("cnpPenaltyDialog", 10)
        click("btPenaltyDialogSave")
        assertThat(storedRounds(1).single().areTherePenaltiesInDb()).isFalse()
        view("btPenaltyDialogSave").assertIsDisplayed()
        // Not divided, any amount is accepted.
        click("cbPenaltyDialog")
        click("btPenaltyDialogSave")
        awaitData { storedRounds(1).single().areTherePenaltiesInDb() }
        with(storedRounds(1).single()) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(0, -10, 0, 0).inOrder()
        }
    }

    // endregion

    // region Game list & game lifecycle

    @Test
    fun deleteRound_fromTheList() = appTest(GameScenarios.selfPicks) {
        val gameId = GameScenarios.selfPicks.game.gameId
        openGame(GameScenarios.selfPicks)
        click("tabGameLIST")
        view(
            hasTestTag("llGameListItemContainer") and
                hasAnyDescendant(hasTestTag("tvGameListItemRoundNumber") and hasText("1"))
        ).performClick()
        awaitView("action_delete_round")
        click("action_delete_round")
        confirmAlertDialog()
        awaitRounds(gameId, GameScenarios.selfPicks.rounds.size - 1)
        assertThat(storedRounds(gameId).map { it.winnerInitialSeat }).containsExactly(SOUTH, WEST, NORTH, null).inOrder()
    }

    @Test
    fun endGame_thenRankingShowsAndResume() = appTest(GameScenarios.selfPicks) {
        val gameId = GameScenarios.selfPicks.game.gameId
        openGame(GameScenarios.selfPicks)
        openMenuItem("action_end_game")
        awaitData { storedGame(gameId).isEnded }

        // MS2 shows the ranking from the table FAB once the game has ended
        if (!exists("btRankingDialogResume")) {
            awaitView("fabGameTable")
            click("fabGameTable")
        }
        awaitView("btRankingDialogResume")
        view("tvRankingDialogPlayer1Name").assertTextEquals("David")
        view("tvRankingDialogPlayer1Points").assertTextEquals("4")
        view("tvRankingDialogBestHandPlayerName").assertTextEquals("David")
        view("btRankingDialogResume").assertIsDisplayed()
        click("btRankingDialogResume")
        awaitData { !storedGame(gameId).isEnded }
        // MS2 asks for an in-app review when a game ends
        assertThat(platformActions.calls).contains("requestInAppReview")
    }

    @Test
    fun sixteenDraws_endTheGameAndShowTheRanking() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        repeat(16) {
            clickSeat("iGameTableSeatEast")
            click("btHandActionsDialogDraw")
            awaitRounds(1, if (it < 15) it + 2 else 16)
            if (it < 15) {
                // After each hand the list is shown: go back to the table.
                mainClock.advanceTimeBy(1_000)
                click("tabGameTABLE")
                waitForIdle()
            }
        }
        awaitData { storedGame(1).isEnded }
        val finished = storedGame(1)
        assertThat(finished.uiRounds).hasSize(16)
        assertThat(finished.isEnded).isTrue()
        awaitView("btRankingDialogClose")
        assertThat(exists("btRankingDialogResume")).isFalse()
        awaitData { seatsArrangement() == SeatsArrangement.RANKING }
    }

    @Test
    fun editNames_fromTheMenu() = appTest(GameScenarios.penalties) {
        val gameId = GameScenarios.penalties.game.gameId
        openGame(GameScenarios.penalties)
        openMenuItem("action_edit_names")
        view("tietNamesDialogGameName").performTextReplacement("Renamed \"game\"")
        view("tietNamesDialogEast").performTextReplacement(" Eva ")
        click("btNamesDialogSave")
        awaitData { storedGame(gameId).gameName == "Renamed game" }
        val updated = storedGame(gameId)
        assertThat(updated.gameName).isEqualTo("Renamed game")
        assertThat(updated.nameP1).isEqualTo("Eva")
        // The table shows the new name once the game flow emits it
        waitUntil(timeoutMillis = 5_000) { runCatching { view("tvGameTableGameName").assertTextEquals("Renamed game") }.isSuccess }
        view("tvGameTableGameName").assertTextEquals("Renamed game")
    }

    @Test
    fun seatsOrientation_menuCyclesArrangements() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        assertThat(seatsArrangement()).isEqualTo(SeatsArrangement.DOWN)
        click("action_seats_orientation")
        awaitData { seatsArrangement() == SeatsArrangement.OUT }
        click("action_seats_orientation")
        awaitData { seatsArrangement() == SeatsArrangement.RANKING }
        awaitView("iTableSeatsByRanking")
        view("iTableSeatsByRanking").assertIsDisplayed()
        click("action_seats_orientation")
        awaitData { seatsArrangement() == SeatsArrangement.DOWN }
        awaitView("iTableSeatsBySeat")
        view("iTableSeatsBySeat").assertIsDisplayed()
    }

    @Test
    fun diceDialog_opensFromTheTable() = appTest(GameScenarios.newGame) {
        openGame(GameScenarios.newGame)
        click("fabGameTable")
        awaitView("tvDiceDialogFirst")
    }

    @Test
    fun diffsFeature_canBeDisabledFromTheMenu() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        view("btTableSeatsShowDiffs").assertIsDisplayed()
        openMenuItem("action_disable_diffs_calcs")
        waitUntil(timeoutMillis = 5_000) { !exists("btTableSeatsShowDiffs") }
        openMenuItem("action_enable_diffs_calcs")
        awaitView("btTableSeatsShowDiffs")
        view("btTableSeatsShowDiffs").assertIsDisplayed()
    }

    // endregion
}
