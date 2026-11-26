package com.etologic.mahjongscoring3.domain.model

enum class ShareGameOptions {
    TEXT,
    CSV,
    JSON;

    companion object {
        /** Option by its position in the share dialog, as Mahjong Scoring 2. */
        fun fromIndex(index: Int) = when (index) {
            0 -> TEXT
            1 -> CSV
            2 -> JSON
            else -> throw IllegalArgumentException()
        }
    }
}
