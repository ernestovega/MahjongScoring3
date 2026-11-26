package com.etologic.mahjongscoring3.domain.utils

/** Unicode NFC normalization (java.text.Normalizer.Form.NFC in Mahjong Scoring 2). */
expect fun String.toNfc(): String
