package com.etologic.mahjongscoring3.domain.utils

import platform.Foundation.NSString
import platform.Foundation.precomposedStringWithCanonicalMapping

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun String.toNfc(): String = (this as NSString).precomposedStringWithCanonicalMapping
