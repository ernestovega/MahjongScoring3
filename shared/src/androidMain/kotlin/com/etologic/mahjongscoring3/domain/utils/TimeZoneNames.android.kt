package com.etologic.mahjongscoring3.domain.utils

import kotlinx.datetime.TimeZone
import java.util.Date
import java.util.Locale

actual fun timeZoneAbbreviation(timeZone: TimeZone, epochMillis: Long): String {
    val javaTimeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    // Same call as java.util.Date.toString()
    return javaTimeZone.getDisplayName(javaTimeZone.inDaylightTime(Date(epochMillis)), java.util.TimeZone.SHORT, Locale.US)
}
