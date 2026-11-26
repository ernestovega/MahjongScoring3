package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import com.etologic.mahjongscoring3.data.database.AppDatabase
import com.etologic.mahjongscoring3.data.database.buildAppDatabase
import com.etologic.mahjongscoring3.data.settings.createDataStore
import com.etologic.mahjongscoring3.di.dataModule
import com.etologic.mahjongscoring3.di.domainModule
import com.etologic.mahjongscoring3.di.uiModule
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.testutil.GameScenario
import com.etologic.mahjongscoring3.testutil.testDatabaseBuilder
import com.etologic.mahjongscoring3.testutil.testFilePath
import com.etologic.mahjongscoring3.testutil.testSqliteDriver
import com.etologic.mahjongscoring3.ui.main.App
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

/**
 * Port of MS2 UiTestBase: the real App() composable, the real Koin graph and a real Room database
 * (a temporary file) seeded from [GameScenario]s. Views are found by the MS2 view ids, which MS3
 * composables carry as test tags.
 *
 * MS2 ran on Robolectric only; these run on Android (Robolectric) and on the iOS simulator.
 */
@OptIn(ExperimentalTestApi::class)
abstract class UiTestBase : UiPlatformTest() {

    protected lateinit var database: AppDatabase
    protected lateinit var games: GamesRepository
    protected lateinit var rounds: RoundsRepository
    protected lateinit var settings: SettingsRepository

    /** Starts the app with the given [seed] and settings, runs [block], and cleans everything up. */
    protected fun appTest(
        vararg seed: GameScenario,
        seatsArrangement: SeatsArrangement? = null,
        isDiffsFeatureEnabled: Boolean? = null,
        language: String = "en",
        block: ComposeUiTest.() -> Unit,
    ) = runAppUiTest {
        val id = nextId++
        val db = buildAppDatabase(testDatabaseBuilder(testFilePath("ui-$id.db")), testSqliteDriver())
        val dataStore = createDataStore { testFilePath("ui-$id.preferences_pb") }
        stopKoin()
        val koin = startKoin {
            modules(module { single { db }; single { dataStore } }, dataModule, domainModule, uiModule)
        }.koin
        database = db
        games = koin.get()
        rounds = koin.get()
        settings = koin.get()
        try {
            runBlocking {
                settings.saveLanguage(language)
                seatsArrangement?.let { settings.saveSeatsArrangement(it) }
                isDiffsFeatureEnabled?.let { settings.saveDiffsCalcsFeatureEnabled(it) }
                seed.forEach { scenario ->
                    games.insertOne(scenario.game).getOrThrow()
                    scenario.rounds.forEach { rounds.insertOne(it).getOrThrow() }
                }
            }
            setContent { UiTestHost { App() } }
            waitForIdle()
            block()
        } finally {
            stopKoin()
            db.close()
        }
    }

    // region Data

    protected fun storedRounds(gameId: GameId): List<RoundRecord> = runBlocking { rounds.getGameRounds(gameId).getOrThrow() }

    protected fun storedGame(gameId: GameId): UiGame = runBlocking { GetOneGameFlowUseCase(games, rounds)(gameId).first() }

    protected fun allGames(): List<GameRecord> = runBlocking { games.getAllFlow().first() }

    protected fun seatsArrangement(): SeatsArrangement = runBlocking { settings.seatsArrangementFlow.first() }

    /** Room and DataStore write on background threads: wait for the condition instead of asserting at once. */
    protected fun ComposeUiTest.awaitData(timeoutMillis: Long = 5_000, condition: () -> Boolean) {
        waitUntil(timeoutMillis = timeoutMillis) { condition() }
        waitForIdle()
    }

    // endregion

    // region Finders (MS2 view ids)

    /** A node by MS2 view id. The unmerged tree is used so views inside clickable containers can be found. */
    protected fun ComposeUiTest.view(tag: String): SemanticsNodeInteraction = onNode(hasTestTag(tag), useUnmergedTree = true)

    /** A view inside another one, like Espresso isDescendantOfA(withId(parent)). */
    protected fun ComposeUiTest.view(tag: String, insideOf: String): SemanticsNodeInteraction =
        onNode(hasTestTag(tag) and hasAnyAncestor(hasTestTag(insideOf)), useUnmergedTree = true)

    protected fun ComposeUiTest.view(matcher: SemanticsMatcher): SemanticsNodeInteraction = onNode(matcher, useUnmergedTree = true)

    protected fun ComposeUiTest.exists(tag: String): Boolean =
        onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()

    protected fun ComposeUiTest.click(tag: String) {
        view(tag).performClick()
        waitForIdle()
    }

    protected fun ComposeUiTest.click(tag: String, insideOf: String) {
        view(tag, insideOf).performClick()
        waitForIdle()
    }

    /** Waits for a view to appear (screen transitions, dialogs after a database write). */
    protected fun ComposeUiTest.awaitView(tag: String, timeoutMillis: Long = 5_000) {
        waitUntil(timeoutMillis = timeoutMillis) { exists(tag) }
    }

    // endregion

    // region Navigation

    /** MS2 openGame(): here the game is opened like a user does, from its card in Old games. */
    protected fun ComposeUiTest.openGame(scenario: GameScenario) {
        awaitView("cvOldGameItem")
        onNode(
            hasTestTag("cvOldGameItem") and androidx.compose.ui.test.hasAnyDescendant(
                androidx.compose.ui.test.hasText(scenario.game.gameName)
            ),
            useUnmergedTree = true,
        ).performClick()
        awaitView("tabLayoutGame")
        waitForIdle()
    }

    protected fun ComposeUiTest.openDrawer() {
        click("btToolbarNavigation")
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
    }

    protected fun ComposeUiTest.openMenuItem(tag: String) {
        click("action_overflow")
        awaitView(tag)
        click(tag)
    }

    /** Types [points] on the numeric keypad [numPadTag] (MS2 CustomNumPad). */
    protected fun ComposeUiTest.typePoints(numPadTag: String, points: Int) {
        points.toString().forEach { digit -> click("btCustomNumPad$digit", insideOf = numPadTag) }
    }

    // endregion

    // region Screenshots

    /**
     * Captures the screen into build/screenshots/<platform>/<name>.png. When a dialog or menu is showing
     * it is captured too, as <name>_dialog.png (MS2 Roborazzi captured both in one image).
     */
    protected fun ComposeUiTest.screenshot(name: String) {
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
        val roots = onAllNodes(isRoot())
        val count = roots.fetchSemanticsNodes().size
        saveScreenshot(name, roots[0].captureToImage())
        if (count > 1) saveScreenshot("${name}_dialog", roots[count - 1].captureToImage())
    }

    // endregion

    private companion object {
        var nextId = 0
    }
}
