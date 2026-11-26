package com.etologic.mahjongscoring3.ms2

import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.NOT_SET_ROUND_ID
import com.etologic.mahjongscoring3.domain.model.RankingTableHelper
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.TableWinds.EAST
import com.etologic.mahjongscoring3.domain.model.TableWinds.NONE
import com.etologic.mahjongscoring3.domain.model.TableWinds.SOUTH
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiRound
import com.etologic.mahjongscoring3.testutil.assertThat
import kotlin.test.Test

/**
 * Port of MS2 UiRoundTest and RankingTableHelperTest.
 *
 * Not ported: UiRound.getCopy/compareIdTo/compareContentsTo/areEqual (RecyclerView DiffUtil helpers of MS2;
 * MS3 rounds are data classes and Compose diffs them by key).
 */
class UiRoundAndRankingTest {

    private fun round(roundId: Long = 1, gameId: Long = 1, points: Int = 8, p1: Int = 0) =
        UiRound(gameId, roundId, EAST, SOUTH, points, p1, 0, 0, 0)

    @Test
    fun defaultRoundIsOngoingWithoutPointsOrPenalties() {
        val r = UiRound.empty()
        assertThat(r.isOngoing()).isTrue()
        assertThat(r.areTherePenalties()).isFalse()
        assertThat(r.gameId).isEqualTo(NOT_SET_GAME_ID)
        assertThat(r.roundId).isEqualTo(NOT_SET_ROUND_ID)
        assertThat(r.handPoints).isEqualTo(0)
    }

    @Test
    fun aRoundIsOngoingOnlyWhileItHasNoWinnerADrawIsFinished() {
        assertThat(UiRound(1, 1, null, null, 0, 0, 0, 0, 0).isOngoing()).isTrue()
        assertThat(UiRound(1, 1, NONE, NONE, 0, 0, 0, 0, 0).isOngoing()).isFalse()
        assertThat(round().isOngoing()).isFalse()
    }

    @Test
    fun anyNonZeroPenaltyCountsAsPenalty() {
        for (i in 0..3) {
            val p = IntArray(4).also { it[i] = -5 }
            assertThat(UiRound(1, 1, null, null, 0, p[0], p[1], p[2], p[3]).areTherePenalties()).isTrue()
        }
    }

    // RankingTableHelperTest: table points (4/2/1/0) and how ties split them

    private fun gameWithTotals(p1: Int, p2: Int, p3: Int, p4: Int, vararg extraRounds: UiRound) = UiGame(
        gameId = 1, nameP1 = "A", nameP2 = "B", nameP3 = "C", nameP4 = "D",
        startDate = 0, endDate = null, gameName = "g",
        uiRounds = extraRounds.toList() + UiRound(1, 99, null, null, 0, p1, p2, p3, p4),
    )

    private data class Case(val name: String, val scores: List<Int>, val names: List<String>, val points: List<String>)

    @Test
    fun tablePoints() {
        listOf(
            Case("no ties", listOf(10, 30, -20, -20 + 1), listOf("B", "A", "D", "C"), listOf("4", "2", "1", "0")),
            Case("all tied", listOf(0, 0, 0, 0), listOf("A", "B", "C", "D"), listOf("1.75", "1.75", "1.75", "1.75")),
            Case("first three tied", listOf(5, 5, -15, 5), listOf("A", "B", "D", "C"), listOf("2.33", "2.33", "2.33", "0")),
            Case("last three tied", listOf(-5, 15, -5, -5), listOf("B", "A", "C", "D"), listOf("4", "1", "1", "1")),
            Case("first two tied", listOf(10, 10, 0, -20), listOf("A", "B", "C", "D"), listOf("3", "3", "1", "0")),
            Case("second two tied", listOf(20, 0, 0, -20), listOf("A", "B", "C", "D"), listOf("4", "1.5", "1.5", "0")),
            Case("last two tied", listOf(20, 10, -15, -15), listOf("A", "B", "C", "D"), listOf("4", "2", "0.5", "0.5")),
            Case("two pairs", listOf(10, -10, 10, -10), listOf("A", "C", "B", "D"), listOf("3", "3", "0.5", "0.5")),
        ).forEach { case ->
            val s = case.scores
            val data = RankingTableHelper.generateRankingTable(gameWithTotals(s[0], s[1], s[2], s[3]))!!
            assertThat(data.sortedPlayersRankings.map { it.name }).isEqualTo(case.names)
            assertThat(data.sortedPlayersRankings.map { it.points }).isEqualTo(case.points)
            assertThat(data.sortedPlayersRankings.map { it.score }).isEqualTo(s.sortedDescending())
        }
    }

    @Test
    fun nullGameGivesNull() {
        assertThat(RankingTableHelper.generateRankingTable(null)).isNull()
    }

    @Test
    fun bestHandAndRounds() {
        val noWinner = RankingTableHelper.generateRankingTable(gameWithTotals(0, 0, 0, 0))!!
        assertThat(noWinner.bestHandPlayerPoints).isEqualTo("-")
        assertThat(noWinner.bestHandPlayerName).isEqualTo("-")
        assertThat(noWinner.numRounds).isEqualTo(1)
        assertThat(noWinner.sNumRounds).isEqualTo("1")

        val withWinner = RankingTableHelper.generateRankingTable(
            gameWithTotals(0, 0, 0, 0, UiRound(1, 1, TableWinds.WEST, TableWinds.EAST, 30, 0, 0, 0, 0))
        )!!
        assertThat(withWinner.bestHandPlayerPoints).isEqualTo("30")
        assertThat(withWinner.bestHandPlayerName).isEqualTo("C")
        assertThat(withWinner.numRounds).isEqualTo(2)
    }
}
