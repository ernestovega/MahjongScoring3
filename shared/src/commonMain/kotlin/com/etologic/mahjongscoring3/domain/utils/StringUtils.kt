package com.etologic.mahjongscoring3.domain.utils

/** Trims and removes double quotes, as Mahjong Scoring 2 did before saving names. */
fun normalizeName(name: String?): String =
    name?.trim()
        ?.replace("\"", "")
        ?: ""

/** "+5", "-5" and "0". */
fun Int.toStringSigned(): String = if (this > 0) "+$this" else "$this"

/** Same as String.format("%+d"): "+5", "-5" and "+0". */
fun Int.formatSigned(): String = if (this >= 0) "+$this" else "$this"

fun Int?.toStringOrHyphen(): String = this?.toString() ?: "-"

fun String.sanitizeForFileName(
    replacement: String = "_",
    maxComponentLength: Int = 255,
): String {
    val name = trim()

    val lastDot = name.lastIndexOf('.')
    val hasExtension = lastDot > 0 && lastDot < name.length - 1
    val rawBase = if (hasExtension) name.substring(0, lastDot) else name
    val rawExt = if (hasExtension) name.substring(lastDot + 1) else ""

    fun isInvalid(c: Char): Boolean =
        c in "\\/:*?\"<>|" || c.isISOControl() || c.category == CharCategory.FORMAT

    fun replaceInvalid(s: String): String =
        buildString { s.forEach { c -> if (isInvalid(c)) append(replacement) else append(c) } }

    fun collapseRuns(s: String): String =
        s.replace(Regex(Regex.escape(replacement) + "{2,}"), replacement)

    fun trimTrailingDotsSpaces(s: String): String = s.trimEnd(' ', '.')

    var base = trimTrailingDotsSpaces(collapseRuns(replaceInvalid(rawBase)))
    val ext = collapseRuns(replaceInvalid(rawExt))

    if (base.isEmpty()) base = "file"
    if (base.uppercase() in RESERVED_WINDOWS) base = "_$base"

    fun compose(b: String, e: String): String = if (e.isEmpty()) b else "$b.$e"

    var sanitized = compose(base, ext)
    if (sanitized.length > maxComponentLength) {
        val extPart = if (ext.isEmpty()) "" else ".$ext"
        val allowedBaseLen = (maxComponentLength - extPart.length).coerceAtLeast(1)
        sanitized = compose(base.take(allowedBaseLen), ext)
    }
    if (sanitized.isEmpty()) sanitized = "file"
    return sanitized
}

private val RESERVED_WINDOWS = setOf(
    "CON", "PRN", "AUX", "NUL",
    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9",
)
