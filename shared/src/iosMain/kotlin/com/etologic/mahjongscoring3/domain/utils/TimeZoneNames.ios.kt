package com.etologic.mahjongscoring3.domain.utils

import kotlinx.datetime.TimeZone
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.NSTimeZoneNameStyle
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.localizedName
import platform.Foundation.timeZoneWithName

private val UTC_IDS = setOf("Z", "UTC", "UCT", "Etc/UTC", "Etc/UCT", "Universal", "Etc/Universal", "Zulu", "Etc/Zulu")
private val SHORT_GMT_OFFSET = Regex("""^GMT([+-])(\d{1,2})(?::?(\d{2}))?$""")

/**
 * Same text as Android's `java.util.TimeZone.getDisplayName(daylight, SHORT, Locale.US)` (ICU), which is
 * what Mahjong Scoring 2 writes in its CSV: "UTC" for UTC, "PST"/"PDT" where en_US has an abbreviation
 * and "GMT+01:00" otherwise. NSTimeZone.abbreviation would give "GMT" and "GMT+1".
 */
actual fun timeZoneAbbreviation(timeZone: TimeZone, epochMillis: Long): String {
    if (timeZone.id in UTC_IDS) return "UTC"
    val nsTimeZone = NSTimeZone.timeZoneWithName(timeZone.id) ?: return timeZone.id
    val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
    val style = if (nsTimeZone.isDaylightSavingTimeForDate(date)) {
        NSTimeZoneNameStyle.NSTimeZoneNameStyleShortDaylightSaving
    } else {
        NSTimeZoneNameStyle.NSTimeZoneNameStyleShortStandard
    }
    val name = nsTimeZone.localizedName(style, NSLocale.localeWithLocaleIdentifier("en_US"))
        ?: nsTimeZone.abbreviationForDate(date)
        ?: return timeZone.id
    val offset = SHORT_GMT_OFFSET.matchEntire(name) ?: return name
    val (sign, hours, minutes) = offset.destructured
    return "GMT$sign${hours.padStart(2, '0')}:${minutes.ifEmpty { "00" }}"
}
