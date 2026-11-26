package com.etologic.mahjongscoring3.domain.utils

import java.text.Normalizer

actual fun String.toNfc(): String = Normalizer.normalize(this, Normalizer.Form.NFC)
