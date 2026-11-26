package com.etologic.mahjongscoring3.ms2

import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.ShareGameOptions
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.utils.normalizeName
import com.etologic.mahjongscoring3.domain.utils.prettifyOneLine
import com.etologic.mahjongscoring3.domain.utils.prettifyTwoLines
import com.etologic.mahjongscoring3.domain.utils.sanitizeForFileName
import com.etologic.mahjongscoring3.domain.utils.toFileNameFormat
import com.etologic.mahjongscoring3.domain.utils.toGameNameFormat
import com.etologic.mahjongscoring3.domain.utils.toStringOrHyphen
import com.etologic.mahjongscoring3.domain.utils.toStringSigned
import com.etologic.mahjongscoring3.testutil.assertThat
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Port of MS2 EnumsTest, UtilsTest and AppUtilsTest.
 *
 * Not ported (MS2 only, no MS3 equivalent): writeToFile (MS3 hands the text to the platform share sheet),
 * String.capitalize, List.second/third/fourth and DateTimeUtils.areEqual (unused helpers of the MS2 views).
 */
class EnumsAndUtilsTest {

    // EnumsTest: the numeric codes are persisted (Room, DataStore, JSON exports), they must never change.

    @Test
    fun tableWindsCodes() {
        assertThat(TableWinds.entries.map { it.name to it.code }).containsExactly(
            "NONE" to -1, "EAST" to 0, "SOUTH" to 1, "WEST" to 2, "NORTH" to 3,
        ).inOrder()
        assertThat(TableWinds.asArray.toList()).containsExactly(
            TableWinds.EAST, TableWinds.SOUTH, TableWinds.WEST, TableWinds.NORTH,
        ).inOrder()
    }

    @Test
    fun tableWindsFromCode() {
        assertThat(TableWinds.from(null)).isNull()
        assertThat(TableWinds.from(-1)).isEqualTo(TableWinds.NONE)
        assertThat(TableWinds.from(0)).isEqualTo(TableWinds.EAST)
        assertThat(TableWinds.from(1)).isEqualTo(TableWinds.SOUTH)
        assertThat(TableWinds.from(2)).isEqualTo(TableWinds.WEST)
        assertThat(TableWinds.from(3)).isEqualTo(TableWinds.NORTH)
        assertThat(TableWinds.from(4)).isEqualTo(TableWinds.NONE)
        assertThat(TableWinds.from(-99)).isEqualTo(TableWinds.NONE)
    }

    @Test
    fun seatsArrangementCodesAndFallback() {
        assertThat(SeatsArrangement.entries.map { it.name to it.code }).containsExactly(
            "DOWN" to 0, "OUT" to 1, "RANKING" to 2,
        ).inOrder()
        assertThat(SeatsArrangement.from(0)).isEqualTo(SeatsArrangement.DOWN)
        assertThat(SeatsArrangement.from(1)).isEqualTo(SeatsArrangement.OUT)
        assertThat(SeatsArrangement.from(2)).isEqualTo(SeatsArrangement.RANKING)
        assertThat(SeatsArrangement.from(null)).isEqualTo(SeatsArrangement.DOWN)
        assertThat(SeatsArrangement.from(7)).isEqualTo(SeatsArrangement.DOWN)
    }

    @Test
    fun shareOptionsByDialogIndex() {
        assertThat(ShareGameOptions.fromIndex(0)).isEqualTo(ShareGameOptions.TEXT)
        assertThat(ShareGameOptions.fromIndex(1)).isEqualTo(ShareGameOptions.CSV)
        assertThat(ShareGameOptions.fromIndex(2)).isEqualTo(ShareGameOptions.JSON)
        assertFailsWith<IllegalArgumentException> { ShareGameOptions.fromIndex(3) }
        assertFailsWith<IllegalArgumentException> { ShareGameOptions.fromIndex(-1) }
    }

    // UtilsTest

    @Test
    fun normalizeNameTrimsAndRemovesDoubleQuotes() {
        listOf(
            "Ana" to "Ana",
            "  Ana  " to "Ana",
            "\"Ana\"" to "Ana",
            "An\"a" to "Ana",
            "" to "",
            "   " to "",
            "José Luis" to "José Luis",
        ).forEach { (input, expected) -> assertThat(normalizeName(input)).isEqualTo(expected) }
    }

    @Test
    fun normalizeNullName() {
        assertThat(normalizeName(null)).isEmpty()
    }

    @Test
    fun dateFormatsUseTheTimeZone() {
        val date = 1_736_935_200_000L // 2025-01-15T10:00:00Z, tests run in UTC
        assertThat(date.toGameNameFormat(TimeZone.UTC)).isEqualTo("15/01/2025 10:00")
        assertThat(date.toFileNameFormat(TimeZone.UTC)).isEqualTo("2025_01_15_10_00")
    }

    @Test
    fun sanitizeForFileName() {
        listOf(
            "game" to "game",
            "  game  " to "game",
            "my game.csv" to "my game.csv",
            "a/b\\c:d*e?f\"g<h>i" to "a_b_c_d_e_f_g_h_i",
            "a//b" to "a_b",
            "game. . ." to "game",
            "..." to "file",
            "" to "file",
            "CON" to "_CON",
            "con.txt" to "_con.txt",
            "LPT9" to "_LPT9",
            "COM10" to "COM10",
            ".hidden" to ".hidden",
            "name." to "name",
            "archive.tar.gz" to "archive.tar.gz",
            "Torneo Primavera - Mesa 3" to "Torneo Primavera - Mesa 3",
            "王伟 🀄" to "王伟 🀄",
        ).forEach { (input, expected) -> assertThat(input.sanitizeForFileName()).isEqualTo(expected) }
    }

    @Test
    fun sanitizeRemovesControlCharactersAndSupportsCustomReplacement() {
        assertThat("a\tb\nc\u0000d​e".sanitizeForFileName()).isEqualTo("a_b_c_d_e")
        assertThat("a/b".sanitizeForFileName(replacement = "-")).isEqualTo("a-b")
    }

    @Test
    fun sanitizeNormalizesUnicodeToNfc() {
        val decomposed = "José"
        assertThat(decomposed.sanitizeForFileName()).isEqualTo("José")
    }

    @Test
    fun sanitizeTruncatesKeepingTheExtension() {
        val long = "x".repeat(300) + ".json"
        val result = long.sanitizeForFileName()
        assertThat(result).hasLength(255)
        assertThat(result).endsWith(".json")
        assertThat("abcdef.json".sanitizeForFileName(maxComponentLength = 8)).isEqualTo("abc.json")
        assertThat("abcdef".sanitizeForFileName(maxComponentLength = 3)).isEqualTo("abc")
    }

    // AppUtilsTest

    @Test
    fun signedNumbers() {
        assertThat(5.toStringSigned()).isEqualTo("+5")
        assertThat(0.toStringSigned()).isEqualTo("0")
        assertThat((-5).toStringSigned()).isEqualTo("-5")
    }

    @Test
    fun nullableIntToStringOrHyphen() {
        assertThat(null.toStringOrHyphen()).isEqualTo("-")
        assertThat(0.toStringOrHyphen()).isEqualTo("0")
        assertThat((-3).toStringOrHyphen()).isEqualTo("-3")
    }

    @Test
    fun prettyDates() {
        val date = 1_736_935_200_000L // 2025-01-15T10:00:00Z
        assertThat(date.prettifyOneLine(TimeZone.UTC)).isEqualTo("15/01/2025 10:00")
        assertThat(date.prettifyTwoLines(TimeZone.UTC)).isEqualTo("15/01/2025\n10:00")
    }
}
