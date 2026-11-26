package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import com.etologic.mahjongscoring3.testutil.GameScenarios
import kotlin.test.Test

/**
 * Port of MS2 KnownIssuesTest. In MS2 these tests are @Ignore'd because they fail there (MS2 bugs).
 * They run here to show whether MS3 has the same bug: if they fail, MS3 copies MS2 behaviour exactly.
 */
@OptIn(ExperimentalTestApi::class)
class KnownIssuesTest : UiTestBase() {

    /** MS2 bug: from the Game screen, the bottom navigation 'Old games' item does nothing. */
    @Test
    fun bottomNavigation_fromGameToOldGames() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("bottomNavigation_oldGames")
        awaitView("oldGamesFragment")
        view("oldGamesFragment").assertIsDisplayed()
    }

    /** MS2 bug: after visiting Game, Combinations -> 'Old games' lands on Game. */
    @Test
    fun bottomNavigation_fromCombinationsToOldGames_afterVisitingGame() = appTest(GameScenarios.penalties) {
        openGame(GameScenarios.penalties)
        click("bottomNavigation_combinations")
        awaitView("combinationsFragment")
        click("bottomNavigation_oldGames")
        awaitView("oldGamesFragment")
        view("oldGamesFragment").assertIsDisplayed()
    }
}
