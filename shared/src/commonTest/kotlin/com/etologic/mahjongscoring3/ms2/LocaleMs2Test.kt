package com.etologic.mahjongscoring3.ms2

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import com.etologic.mahjongscoring3.ui.UiTestBase
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Port of MS2 LocalRepositoriesTest "set locale maps app languages and falls back to english":
 * every app language shows its own texts, and a language the app does not have shows English.
 */
@OptIn(ExperimentalTestApi::class)
class LocaleMs2Test : UiTestBase() {

    private fun ComposeUiTest.assertTextShown(text: String) {
        awaitData { onAllNodes(hasText(text), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        assertTrue(onAllNodes(hasText(text), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty(), "'$text' is not shown")
    }

    @Test
    fun chinese() = appTest(language = "zh") { assertTextShown("游戏记录") }

    @Test
    fun dutch() = appTest(language = "nl") { assertTextShown("Vorige spellen") }

    @Test
    fun english() = appTest(language = "en") { assertTextShown("Old games") }

    @Test
    fun french() = appTest(language = "fr") { assertTextShown("Anciennes parties") }

    @Test
    fun spanish() = appTest(language = "es") { assertTextShown("Partidas antiguas") }

    @Test
    fun italian() = appTest(language = "it") { assertTextShown("Giochi precedenti") }

    @Test
    fun unsupportedLanguage_fallsBackToEnglish() = appTest(language = "de") { assertTextShown("Old games") }
}
