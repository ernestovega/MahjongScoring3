package com.etologic.mahjongscoring3.ms2

import android.content.ContentProvider
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.hasAnyDescendant
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring3.data.database.AppDatabase
import com.etologic.mahjongscoring3.data.database.buildAppDatabase
import com.etologic.mahjongscoring3.data.settings.createDataStore
import com.etologic.mahjongscoring3.di.dataModule
import com.etologic.mahjongscoring3.di.domainModule
import com.etologic.mahjongscoring3.di.uiModule
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import com.etologic.mahjongscoring3.platform.LocalPlatformActionsOverride
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.testutil.testDatabaseBuilder
import com.etologic.mahjongscoring3.testutil.testFilePath
import com.etologic.mahjongscoring3.testutil.testSqliteDriver
import com.etologic.mahjongscoring3.ui.FakePlatformActions
import com.etologic.mahjongscoring3.ui.UiTestHost
import com.etologic.mahjongscoring3.ui.main.App
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Port of MS2 DialogLifecycleTest and DialogBindingOnDestroyCrashTest (Android only): the activity is
 * recreated (rotation, "Don't keep activities", process death) while a names dialog is open.
 * MS2 crashed there before its fix (binding read after onDestroyView). After the fix, the dialog and
 * what the user typed are still there after the recreation, so MS3 must do the same.
 *
 * StateRestorationTester disposes the composition and builds it again from the saved instance state,
 * which is what an activity recreation does to Compose content. The ViewModels stay (like in a
 * configuration change).
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DialogRecreationTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var games: GamesRepository
    private lateinit var rounds: RoundsRepository

    @Before
    fun setUp() {
        @Suppress("UNCHECKED_CAST")
        Robolectric.setupContentProvider(
            Class.forName("org.jetbrains.compose.resources.AndroidContextProvider") as Class<ContentProvider>
        )
        val id = System.nanoTime()
        db = buildAppDatabase(testDatabaseBuilder(testFilePath("recreation-$id.db")), testSqliteDriver())
        val dataStore = createDataStore { testFilePath("recreation-$id.preferences_pb") }
        stopKoin()
        val koin = startKoin {
            modules(module { single { db }; single { dataStore } }, dataModule, domainModule, uiModule)
        }.koin
        games = koin.get()
        rounds = koin.get()
        runBlocking { koin.get<SettingsRepository>().saveLanguage("en") }
    }

    @After
    fun tearDown() {
        stopKoin()
        db.close()
    }

    private fun startApp(): StateRestorationTester {
        val tester = StateRestorationTester(compose)
        val platformActions = FakePlatformActions()
        tester.setContent {
            UiTestHost {
                CompositionLocalProvider(LocalPlatformActionsOverride provides platformActions) { App() }
            }
        }
        compose.waitForIdle()
        return tester
    }

    private fun awaitTag(tag: String) {
        compose.waitUntil(5_000) {
            compose.onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun recreate(tester: StateRestorationTester) {
        tester.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
    }

    @Test
    fun newGameDialog_survivesActivityRecreation() {
        val tester = startApp()
        awaitTag("fabOldGames")
        compose.onNodeWithTag("fabOldGames", useUnmergedTree = true).performClick()
        awaitTag("tietNamesDialogEast")
        compose.onNodeWithTag("tietNamesDialogEast", useUnmergedTree = true).performTextReplacement("Pepe")

        recreate(tester)

        awaitTag("btNamesDialogCancel")
        compose.onNodeWithTag("btNamesDialogCancel", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("tietNamesDialogEast", useUnmergedTree = true).assertTextEquals("Pepe")
    }

    @Test
    fun editNamesDialog_survivesActivityRecreation() {
        val scenario = GameScenarios.penalties
        runBlocking {
            games.insertOne(scenario.game).getOrThrow()
            scenario.rounds.forEach { rounds.insertOne(it).getOrThrow() }
        }
        val tester = startApp()
        awaitTag("cvOldGameItem")
        compose.onNode(
            hasTestTag("cvOldGameItem") and hasAnyDescendant(hasText(scenario.game.gameName)),
            useUnmergedTree = true,
        ).performClick()
        awaitTag("tabLayoutGame")
        compose.onNodeWithTag("action_overflow", useUnmergedTree = true).performClick()
        awaitTag("action_edit_names")
        compose.onNodeWithTag("action_edit_names", useUnmergedTree = true).performClick()
        awaitTag("tietNamesDialogEast")
        compose.onNodeWithTag("tietNamesDialogEast", useUnmergedTree = true).performTextReplacement("Pepe")

        recreate(tester)

        awaitTag("btNamesDialogCancel")
        compose.onNodeWithTag("btNamesDialogCancel", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("tietNamesDialogEast", useUnmergedTree = true).assertTextEquals("Pepe")
    }
}
