package com.etologic.mahjongscoring3.domain.utils

import kotlinx.datetime.TimeZone
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeZoneWithName

actual fun timeZoneAbbreviation(timeZone: TimeZone, epochMillis: Long): String {
    val id = if (timeZone.id == "Z") "UTC" else timeZone.id
    val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
    return NSTimeZone.timeZoneWithName(id)?.abbreviationForDate(date) ?: id
}
