package domain.use_cases.utils

import domain.model.PlayerRanking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//import java.io.File

fun normalizeName(name: String?): String =
    name?.trim()
        ?.replace("\"", "")
        ?: ""

//fun writeToCsvFile(fileName: String, fileText: String, directory: File?): File =
//    File(directory, fileName)
//        .apply {
//            writeText(fileText)
//        }

@OptIn(FormatStringsInDatetimeFormats::class)
private fun String.toInstant(): Instant =
    try {
        val formatPattern = "EEE MMM dd HH:mm:ss zzz yyyy"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(formatPattern) }
        dateTimeFormat.parse(this).toInstant(TimeZone.currentSystemDefault())
    } catch (e: IllegalArgumentException) {
        Clock.System.now()
    }

@OptIn(FormatStringsInDatetimeFormats::class)
fun Instant.toFileNameFormat(): String =
    try {
        val formatPattern = "yyyy_MM_dd_HH_mm"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(formatPattern) }
        dateTimeFormat.format(this.toLocalDateTime(TimeZone.currentSystemDefault()))
    } catch (e: IllegalArgumentException) {
        "-"
    }

@OptIn(FormatStringsInDatetimeFormats::class)
fun Instant.prettifyOneLine(): String =
    try {
        val formatPattern = "dd/MM/yyyy HH:mm"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(formatPattern) }
        dateTimeFormat.format(this.toLocalDateTime(TimeZone.currentSystemDefault())).capitalize()
    } catch (e: IllegalArgumentException) {
        "-"
    }

@OptIn(FormatStringsInDatetimeFormats::class)
fun Instant.prettifyTwoLines(): String =
    try {
        val formatPattern = "dd/MM/yyyy\nHH:mm"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(formatPattern) }
        dateTimeFormat.format(this.toLocalDateTime(TimeZone.currentSystemDefault())).capitalize()
    } catch (e: IllegalArgumentException) {
        "-"
    }

fun areEqual(date1: Instant?, date2: Instant?): Boolean {
    if (date1 == null && date2 == null) return true
    return if (date1 == null || date2 == null) false else date1 === date2
}

fun <T> List<T>.second(): T {
    if (size < 2)
        throw NoSuchElementException("Not enough elements")
    return this[1]
}

fun <T> List<T>.third(): T {
    if (size < 3)
        throw NoSuchElementException("Not enough elements")
    return this[2]
}

fun <T> List<T>.fourth(): T {
    if (size < 4)
        throw NoSuchElementException("Not enough elements")
    return this[3]
}

fun <T> Array<T>.second(): T {
    if (size < 2)
        throw NoSuchElementException("Not enough elements")
    return this[1]
}

fun <T> Array<T>.third(): T {
    if (size < 3)
        throw NoSuchElementException("Not enough elements")
    return this[2]
}

fun <T> Array<T>.fourth(): T {
    if (size < 4)
        throw NoSuchElementException("Not enough elements")
    return this[3]
}

fun IntArray.second(): Int {
    if (size < 2)
        throw NoSuchElementException("Not enough elements")
    return this[1]
}

fun IntArray.third(): Int {
    if (size < 3)
        throw NoSuchElementException("Not enough elements")
    return this[2]
}

fun IntArray.fourth(): Int {
    if (size < 4)
        throw NoSuchElementException("Not enough elements")
    return this[3]
}

fun Int?.toStringOrHyphen(): String = this?.toString() ?: "-"

fun PlayerRanking.toSignedString() = "$name    $points    (${score.toSignedString()})"

fun Int.toSignedString() = if (this > 0) "+$this" else "$this"

fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

inline fun <reified T> T?.toJson(): String = Json.encodeToString(this)

inline fun <reified T> String.fromJson(): T = Json.decodeFromString(this)
