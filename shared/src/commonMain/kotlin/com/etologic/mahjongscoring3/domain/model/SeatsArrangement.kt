package com.etologic.mahjongscoring3.domain.model

enum class SeatsArrangement(val code: Int) {
    DOWN(0),
    OUT(1),
    RANKING(2);

    fun next(): SeatsArrangement =
        when (this) {
            DOWN -> OUT
            OUT -> RANKING
            RANKING -> DOWN
        }

    companion object {
        fun from(code: Int?): SeatsArrangement =
            when (code) {
                0 -> DOWN
                1 -> OUT
                2 -> RANKING
                else -> DOWN
            }
    }
}
