package com.etologic.mahjongscoring3.ui.combinations

import com.etologic.mahjongscoring3.platform.CHINESE
import com.etologic.mahjongscoring3.platform.DUTCH
import com.etologic.mahjongscoring3.platform.ENGLISH
import com.etologic.mahjongscoring3.platform.FRENCH
import com.etologic.mahjongscoring3.platform.ITALIAN
import com.etologic.mahjongscoring3.platform.SPANISH
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.allDrawableResources
import com.etologic.mahjongscoring3.resources.allStringResources
import com.etologic.mahjongscoring3.testutil.GoldenFiles
import com.etologic.mahjongscoring3.testutil.Goldens
import com.etologic.mahjongscoring3.testutil.assertThat
import kotlin.test.Test

/**
 * Port of the catalogue part of MS2 LocalRepositoriesTest: the 81 MCR combinations in every supported
 * language must match golden/combinations/<language>.txt (points | localized name | image | localized description).
 *
 * MS2 resolved the strings through Android resources; here they are read from MS3's compose resource
 * files (values-xx/strings.xml, falling back to values/ like the resources runtime does), so the test
 * checks MS3's catalogue, images and translations without needing a running UI.
 */
class CombinationsCatalogueTest {

    private val languages = listOf(ENGLISH, SPANISH, FRENCH, ITALIAN, DUTCH, CHINESE)

    private fun strings(language: String): Map<String, String> {
        val folder = if (language == ENGLISH) "values" else "values-$language"
        val xml = GoldenFiles.readOrNull("../../../commonMain/composeResources/$folder/strings.xml")
            ?: error("Missing strings for $language")
        return Regex("""<string name="([^"]+)"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            .findAll(xml)
            .associate { it.groupValues[1] to unescape(it.groupValues[2]) }
    }

    private fun unescape(raw: String): String {
        val sb = StringBuilder()
        var i = 0
        val text = raw
            .replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'")
            .replace(Regex("&#(\\d+);")) { it.groupValues[1].toInt().toChar().toString() }
            .replace("&amp;", "&")
        while (i < text.length) {
            val c = text[i]
            if (c == '\\' && i + 1 < text.length) {
                when (val next = text[i + 1]) {
                    'n' -> { sb.append('\n'); i += 2 }
                    't' -> { sb.append('\t'); i += 2 }
                    'u' -> { sb.append(text.substring(i + 2, i + 6).toInt(16).toChar()); i += 6 }
                    else -> { sb.append(next); i += 2 }
                }
            } else {
                sb.append(c); i++
            }
        }
        return sb.toString()
    }

    private val stringKeys by lazy { Res.allStringResources.entries.associate { (key, value) -> value to key } }
    private val drawableKeys by lazy { Res.allDrawableResources.entries.associate { (key, value) -> value to key } }

    @Test
    fun combinationsCatalogueInEveryLanguage() {
        val defaults = strings(ENGLISH)
        languages.forEach { language ->
            val localized = defaults + strings(language)
            fun text(resource: org.jetbrains.compose.resources.StringResource): String {
                val key = stringKeys[resource] ?: error("Unknown string resource $resource")
                return localized[key] ?: error("Missing string $key in $language")
            }
            assertThat(allCombinations).hasSize(81)
            assertThat(allCombinations.map { it.points }).isInOrder()
            assertThat(allCombinations.map { it.points }.distinct())
                .containsExactly(1, 2, 4, 6, 8, 12, 16, 24, 32, 48, 64, 88).inOrder()
            allCombinations.forEach {
                // Every combination has either an image or a description.
                assertThat(it.image != null || it.description != null).isTrue()
            }
            val catalogue = allCombinations.joinToString("\n", postfix = "\n") { c ->
                val image = c.image?.let { "@drawable/" + (drawableKeys[it] ?: error("Unknown drawable $it")) } ?: "-"
                "${c.points} | ${text(c.name)} | $image | ${c.description?.let(::text) ?: "-"}"
            }
            Goldens.assertMatches("combinations/$language.txt", catalogue)
        }
    }

    @Test
    fun stringsExistInEveryLanguage() {
        val keys = listOf("app_name", "old_games", "combinations", "game")
        languages.forEach { language ->
            val localized = strings(language)
            keys.forEach { key ->
                assertThat(localized[key].orEmpty().isNotEmpty()).isTrue()
            }
        }
    }

    @Test
    fun everyLanguageTranslatesTheSameKeysAsEnglish() {
        // Not in MS2: catches a string added in English but forgotten in another language.
        val english = strings(ENGLISH).keys
        languages.filter { it != ENGLISH }.forEach { language ->
            val missing = english - strings(language).keys
            assertThat(missing).isEmpty()
        }
    }
}
