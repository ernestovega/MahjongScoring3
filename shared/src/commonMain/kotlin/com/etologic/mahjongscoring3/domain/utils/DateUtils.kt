package com.etologic.mahjongscoring3.domain.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun interface TimeProvider {
    fun nowMillis(): Long

    companion object {
        val System = TimeProvider { Clock.System.now().toEpochMilliseconds() }
    }
}

private fun Long.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)

private fun Int.pad2(): String = toString().padStart(2, '0')

/** "dd/MM/yyyy HH:mm", used as default game name (same as Mahjong Scoring 2). */
fun Long.toGameNameFormat(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    with(toLocalDateTime(timeZone)) { "${day.pad2()}/${month.number.pad2()}/$year ${hour.pad2()}:${minute.pad2()}" }

/** "yyyy_MM_dd_HH_mm", used for exported files names. */
fun Long.toFileNameFormat(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    with(toLocalDateTime(timeZone)) { "${year}_${month.number.pad2()}_${day.pad2()}_${hour.pad2()}_${minute.pad2()}" }

fun Long.prettifyOneLine(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    toGameNameFormat(timeZone)

fun Long.prettifyTwoLines(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    with(toLocalDateTime(timeZone)) { "${day.pad2()}/${month.number.pad2()}/$year\n${hour.pad2()}:${minute.pad2()}" }

/** Same text as java.util.Date.toString(), used by Mahjong Scoring 2 CSV exports: "Wed Jan 15 11:00:00 UTC 2025". */
fun Long.toCsvDateFormat(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    with(toLocalDateTime(timeZone)) {
        val dayName = dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val monthName = month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        "$dayName $monthName ${day.pad2()} ${hour.pad2()}:${minute.pad2()}:${second.pad2()} ${timeZoneAbbreviation(timeZone, this@toCsvDateFormat)} $year"
    }
