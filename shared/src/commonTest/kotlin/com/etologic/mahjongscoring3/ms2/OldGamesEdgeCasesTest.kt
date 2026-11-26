package com.etologic.mahjongscoring3.ms2

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.ui.UiTestBase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Port of MS2 UiEdgeCasesTest.oldGames_withAGameWithoutRounds_listsTheOtherGamesAndKeepsUpdating
 * (MS2 fix 38a896f): a game without rounds is skipped instead of hiding the whole Old games list.
 */
@OptIn(ExperimentalTestApi::class)
class OldGamesEdgeCasesTest : UiTestBase() {

    private fun ComposeUiTest.isListed(gameName: String) =
        onAllNodes(hasTestTag("cvOldGameItem") and hasAnyDescendant(hasText(gameName)), useUnmergedTree = true)
            .fetchSemanticsNodes().isNotEmpty()

    private fun ComposeUiTest.oldGamesCount() =
        onAllNodes(hasTestTag("cvOldGameItem"), useUnmergedTree = true).fetchSemanticsNodes().size

    @Test
    fun oldGames_withAGameWithoutRounds_listsTheOtherGamesAndKeepsUpdating() = appTest(GameScenarios.penalties) {
        runBlocking { games.insertOne(GameScenarios.newGame.game.copy(gameId = 50, gameName = "no rounds")).getOrThrow() }
        awaitData { isListed("penalties") }
        assertEquals(1, oldGamesCount())

        val fullGame = GameScenarios.fullGame
        runBlocking {
            games.insertOne(fullGame.game).getOrThrow()
            fullGame.rounds.forEach { rounds.insertOne(it).getOrThrow() }
        }
        awaitData { isListed(fullGame.game.gameName) }
        assertEquals(2, oldGamesCount())
    }
}
