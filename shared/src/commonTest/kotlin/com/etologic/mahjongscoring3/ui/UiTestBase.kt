package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
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
import com.etologic.mahjongscoring3.platform.LocalPlatformActionsOverride
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.WindowInsets
import com.etologic.mahjongscoring3.ui.main.AppInsets
import com.etologic.mahjongscoring3.ui.main.LocalAppInsetsOverride
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

    /** Records what the app asked the operating system to do, instead of doing it. */
    protected val platformActions = FakePlatformActions()

    /** Starts the app with the given [seed] and settings, runs [block], and cleans everything up. */
    protected fun appTest(
        vararg seed: GameScenario,
        seatsArrangement: SeatsArrangement? = null,
        isDiffsFeatureEnabled: Boolean? = null,
        language: String = "en",
        restorable: Boolean = false,
        screen: TestScreen = TestScreen.PHONE,
        block: ComposeUiTest.() -> Unit,
    ) = runAppUiTest(screen) {
        val id = nextId++
        appInsets = null
        platformActions.calls.clear()
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
            val content: @androidx.compose.runtime.Composable () -> Unit = {
                UiTestHost {
                    CompositionLocalProvider(
                        LocalPlatformActionsOverride provides platformActions,
                        LocalAppInsetsOverride provides appInsets,
                    ) { App() }
                }
            }
            appContent = content
            if (restorable) {
                // The content can be rebuilt from its saved state, like an activity recreation (see [recreate])
                val restorer = StateRestorer().also { stateRestorer = it }
                setContent(restorableContent(restorer, window = null, content))
            } else {
                stateRestorer = null
                setContent(content)
            }
            waitForIdle()
            block()
        } finally {
            stopKoin()
            db.close()
        }
    }

    private var stateRestorer: StateRestorer? = null

    /** Fake window insets for the app (null: the real ones of the test window, none). See [sendInsets]. */
    private var appInsets by mutableStateOf<AppInsets?>(null)

    /**
     * Sends fake system bar, display cutout and keyboard insets to the app, like MS2 EdgeToEdgeTest does with
     * ViewCompat.dispatchApplyWindowInsets. Each call replaces the insets of the previous one.
     */
    protected fun ComposeUiTest.sendInsets(
        systemBars: WindowInsets,
        displayCutout: WindowInsets = WindowInsets(0, 0, 0, 0),
        ime: WindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        appInsets = AppInsets(systemBars, displayCutout, ime)
        waitForIdle()
    }

    private var appContent: (@androidx.compose.runtime.Composable () -> Unit)? = null

    /**
     * Every test window gets its content from this one call site. The keys of rememberSaveable come from the
     * position in the composition, so a different call site in the new window would restore nothing.
     */
    private fun restorableContent(
        restorer: StateRestorer,
        window: Any?,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ): @androidx.compose.runtime.Composable () -> Unit = { restorer.Content(window, content) }

    /**
     * A rotation (or a window resize) on Android 16 large screens: the activity is recreated with the size of
     * [screen], and [block] goes on with the test there. Needs appTest(restorable = true).
     * On iOS the test window cannot change its size, so the content moves to a new test window (see [withScreen]).
     */
    protected fun ComposeUiTest.rotate(screen: TestScreen, block: ComposeUiTest.() -> Unit) {
        val restorer = checkNotNull(stateRestorer) { "rotate() needs appTest(restorable = true)" }
        val content = checkNotNull(appContent)
        val oldWindow = this
        with(restorer) { saveAndRemove() }
        withScreen(screen) {
            if (this !== oldWindow) {
                val newWindow = this
                restorer.moveTo(newWindow)
                setContent(restorableContent(restorer, newWindow, content))
            }
            with(restorer) { restore() }
            block()
        }
    }

    /**
     * Saves the state of the content, disposes it and builds it again from the saved state, like an
     * activity recreation (rotation, "Don't keep activities"). The ViewModels stay. Needs appTest(restorable = true).
     */
    protected fun ComposeUiTest.recreate() {
        with(checkNotNull(stateRestorer) { "recreate() needs appTest(restorable = true)" }) { saveAndRestore() }
    }

    // region Data

    protected fun storedRounds(gameId: GameId): List<RoundRecord> = runBlocking { rounds.getGameRounds(gameId).getOrThrow() }

    protected fun storedGame(gameId: GameId): UiGame = runBlocking { GetOneGameFlowUseCase(games, rounds)(gameId).first() }

    protected fun allGames(): List<GameRecord> = runBlocking { games.getAllFlow().first() }

    protected fun seatsArrangement(): SeatsArrangement = runBlocking { settings.seatsArrangementFlow.first() }

    /** Room and DataStore write on background threads: wait for the condition instead of asserting at once. */
    protected fun ComposeUiTest.awaitData(timeoutMillis: Long = 5_000, condition: () -> Boolean) {
        waitUntil(timeoutMillis = timeoutMillis) { pumpMainThread(); condition() }
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

    /** Waits for the view first: under Robolectric the data (and so the view) can arrive after the screen. */
    protected fun ComposeUiTest.click(tag: String) {
        awaitView(tag)
        view(tag).performClick()
        waitForIdle()
    }

    protected fun ComposeUiTest.click(tag: String, insideOf: String) {
        waitUntil(timeoutMillis = 5_000) {
            pumpMainThread()
            onAllNodes(hasTestTag(tag) and hasAnyAncestor(hasTestTag(insideOf)), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
        view(tag, insideOf).performClick()
        waitForIdle()
    }

    /** Waits for a view to appear (screen transitions, dialogs after a database write). */
    protected fun ComposeUiTest.awaitView(tag: String, timeoutMillis: Long = 5_000) {
        waitUntil(timeoutMillis = timeoutMillis) { pumpMainThread(); exists(tag) }
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

    /** Captures the screen into build/screenshots/<platform>/<name>.png, with MS2 screenshot names. */
    protected fun ComposeUiTest.screenshot(name: String) {
        mainClock.advanceTimeBy(1_000)
        waitForIdle()
        captureScreen(name)
    }

    // endregion

    private companion object {
        var nextId = 0
    }
}
