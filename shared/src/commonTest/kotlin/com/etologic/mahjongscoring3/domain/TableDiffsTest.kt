package com.etologic.mahjongscoring3.domain

import com.etologic.mahjongscoring3.domain.model.TableDiffs
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MIN_MCR_POINTS
import com.etologic.mahjongscoring3.domain.model.getNeededPointsByDirectHu
import com.etologic.mahjongscoring3.domain.model.getNeededPointsByIndirectHu
import com.etologic.mahjongscoring3.domain.model.getNeededPointsBySelfPick
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TableDiffsTest {

    private fun sorted(t: TableDiffs) = t.seatsDiffs.sortedByDescending { it.points }

    @Test
    fun first_and_second_are_equal() {
        val diffs = sorted(TableDiffs(64, 64, 0, -128))
        assertEquals(MIN_MCR_POINTS, diffs[0].pointsToBeFirst?.bySelfPick)
        assertEquals(MIN_MCR_POINTS, diffs[1].pointsToBeFirst?.byDirectHu)
        assertEquals(MIN_MCR_POINTS, diffs[1].pointsToBeFirst?.byIndirectHu)
    }

    @Test
    fun second_and_third_are_equal() {
        val diffs = sorted(TableDiffs(128, 32, 32, -192))
        assertEquals(MIN_MCR_POINTS, diffs[1].pointsToBeSecond?.bySelfPick)
        assertEquals(MIN_MCR_POINTS, diffs[2].pointsToBeSecond?.byIndirectHu)
    }

    @Test
    fun third_and_fourth_are_equal() {
        val diffs = sorted(TableDiffs(96, 32, -64, -64))
        assertEquals(MIN_MCR_POINTS, diffs[2].pointsToBeThird?.bySelfPick)
        assertEquals(MIN_MCR_POINTS, diffs[3].pointsToBeThird?.byDirectHu)
    }

    @Test
    fun first_second_and_third_fourth_are_equal() {
        val diffs = sorted(TableDiffs(64, 64, -64, -64))
        val diff3rdTo1st = abs(diffs[0].points - diffs[2].points)
        assertEquals(MIN_MCR_POINTS, diffs[0].pointsToBeFirst?.bySelfPick)
        assertEquals(diff3rdTo1st.getNeededPointsBySelfPick(), diffs[2].pointsToBeFirst?.bySelfPick)
        assertEquals(diff3rdTo1st.getNeededPointsByDirectHu(), diffs[3].pointsToBeFirst?.byDirectHu)
        assertEquals(MIN_MCR_POINTS, diffs[3].pointsToBeThird?.byIndirectHu)
    }

    @Test
    fun all_equal() {
        val diffs = TableDiffs(0, 0, 0, 0).seatsDiffs
        diffs.forEach {
            assertEquals(MIN_MCR_POINTS, it.pointsToBeFirst?.bySelfPick)
            assertNull(it.pointsToBeSecond)
            assertNull(it.pointsToBeThird)
        }
    }

    @Test
    fun result_is_sorted_by_seat() {
        val diffs = TableDiffs(-10, 20, 30, -40).seatsDiffs
        assertEquals(listOf(-10, 20, 30, -40), diffs.map { it.points })
        assertNull(diffs[2].pointsToBeFirst)
    }

    @Test
    fun needed_points() {
        val selfPick = listOf(3 to 8, 39 to 8, 52 to 8, 64 to 9, 68 to 10, 87 to 14, 115 to 21, 116 to 22, 117 to 22, 120 to 23, 164 to 34, 213 to 46, 227 to 49, 263 to 58, 299 to 67, 332 to 76, 377 to 87, 409 to 95, 428 to 100)
        val direct = listOf(3 to 8, 39 to 8, 52 to 11, 64 to 17, 68 to 19, 87 to 28, 115 to 42, 116 to 43, 117 to 43, 118 to 44, 120 to 45, 164 to 67, 213 to 91, 227 to 98, 263 to 116, 299 to 134, 332 to 151, 377 to 173, 409 to 189, 428 to 199)
        val indirect = listOf(3 to 8, 39 to 8, 52 to 21, 64 to 33, 68 to 37, 87 to 56, 115 to 84, 120 to 89, 164 to 133, 213 to 182, 227 to 196, 263 to 232, 299 to 268, 332 to 301, 377 to 346, 409 to 378, 428 to 397)
        selfPick.forEach { (diff, needed) -> assertEquals(needed, diff.getNeededPointsBySelfPick(), "self pick $diff") }
        direct.forEach { (diff, needed) -> assertEquals(needed, diff.getNeededPointsByDirectHu(), "direct $diff") }
        indirect.forEach { (diff, needed) -> assertEquals(needed, diff.getNeededPointsByIndirectHu(), "indirect $diff") }
    }
}
