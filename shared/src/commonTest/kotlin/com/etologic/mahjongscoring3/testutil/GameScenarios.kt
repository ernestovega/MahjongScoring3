package com.etologic.mahjongscoring3.testutil

import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.PortableGame
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.toGameRecord
import com.etologic.mahjongscoring3.domain.model.toRoundRecords
import com.etologic.mahjongscoring3.domain.model.toUiGame
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

data class GameScenario(
    val name: String,
    val description: String,
    val game: GameRecord,
    val rounds: List<RoundRecord>,
) {
    fun toUiGame(): UiGame = game.toUiGame(rounds)
}

/**
 * The same deterministic games as Mahjong Scoring 2 testutil/GameScenarios, with the same ids
 * (game ids 1..18 and 101..120, round ids gameId * 100 + n).
 *
 * MS2 builds them in code (its random games use java.util.Random, which is JVM only); MS3 loads the inputs
 * MS2 exported to `golden/scenarios/<name>/game.json`, which is exactly the same data.
 */
object GameScenarios {

    /** 2025-01-15T10:00:00Z. */
    const val BASE_START_MILLIS = 1_736_935_200_000L

    private val json = Json { ignoreUnknownKeys = true }

    private val descriptions: Map<String, String> by lazy {
        Goldens.read("scenarios/INDEX.md").lines()
            .filter { it.startsWith("- ") }
            .associate { line -> line.removePrefix("- ").substringBefore(":") to line.substringAfter(": ") }
    }

    private fun load(id: GameId, name: String): GameScenario {
        val portable = json.decodeFromString(ListSerializer(PortableGame.serializer()), Goldens.read("scenarios/$name/game.json")).single()
        val rounds = portable.rounds.toRoundRecords(id).mapIndexed { index, round -> round.copy(roundId = id * 100 + index + 1) }
        return GameScenario(name, descriptions.getValue(name), portable.toGameRecord(id), rounds)
    }

    val newGame by lazy { load(1, "new_game") }
    val selfPicks by lazy { load(2, "self_picks") }
    val allDiscardPairs by lazy { load(3, "all_discard_pairs") }
    val penalties by lazy { load(4, "penalties") }
    val sixteenDraws by lazy { load(5, "sixteen_draws") }
    val fullGame by lazy { load(6, "full_game") }
    val tieAll4 by lazy { load(7, "tie_all_four") }
    val tieFirst3 by lazy { load(8, "tie_first_three") }
    val tieLast3 by lazy { load(9, "tie_last_three") }
    val tieFirst2 by lazy { load(10, "tie_first_two") }
    val tieSecond2 by lazy { load(11, "tie_second_two") }
    val tieLast2 by lazy { load(12, "tie_last_two") }
    val tiePairs by lazy { load(13, "tie_two_pairs") }
    val specialNames by lazy { load(14, "special_names") }
    val emptyGameName by lazy { load(15, "empty_game_name") }
    val bestHandTie by lazy { load(16, "best_hand_tie") }
    val penaltiesOnly by lazy { load(17, "penalties_only") }
    val bigScores by lazy { load(18, "big_scores") }

    val randomGames: List<GameScenario> by lazy {
        (1..20).map { index -> load(100L + index, "random_${index.toString().padStart(2, '0')}") }
    }

    val handWritten: List<GameScenario> by lazy {
        listOf(
            newGame, selfPicks, allDiscardPairs, penalties, sixteenDraws, fullGame,
            tieAll4, tieFirst3, tieLast3, tieFirst2, tieSecond2, tieLast2, tiePairs,
            specialNames, emptyGameName, bestHandTie, penaltiesOnly, bigScores,
        )
    }

    val all: List<GameScenario> by lazy { handWritten + randomGames }
}
