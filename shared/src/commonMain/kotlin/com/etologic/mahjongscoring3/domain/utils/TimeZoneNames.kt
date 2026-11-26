package com.etologic.mahjongscoring3.domain.utils

import kotlinx.datetime.TimeZone

/**
 * Short name of [timeZone] at [epochMillis] ("UTC", "CET", "CEST"...), as java.util.Date.toString() prints it.
 * Mahjong Scoring 2 CSV exports contain that text, so MS3 reproduces it.
 */
expect fun timeZoneAbbreviation(timeZone: TimeZone, epochMillis: Long): String
