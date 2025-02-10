package ui.common.components

import kotlin.math.ceil

fun Int.getNeededPointsBySelfPick(): Int =
    when {
        this < 64 -> MIN_MCR_POINTS

        this.rem(4) > 0 -> (ceil(this.toFloat() / 4) - MIN_MCR_POINTS).toInt()

        else -> (this / 4) - MIN_MCR_POINTS + 1
    }

fun Int.getNeededPointsByDirectHu(): Int =
    when {
        this < 48 -> MIN_MCR_POINTS

        this.rem(2) > 0 -> ceil((this - 32).toFloat() / 2).toInt()

        else -> ((this - 32) / 2) + 1
    }

fun Int.getNeededPointsByIndirectHu(): Int =
    when {
        this < 40 -> MIN_MCR_POINTS

        else -> this - 32 + 1
    }