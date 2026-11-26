package com.etologic.mahjongscoring3.golden

import com.etologic.mahjongscoring3.domain.FakeGamesRepository
import com.etologic.mahjongscoring3.domain.FakeRoundsRepository
import com.etologic.mahjongscoring3.domain.model.Diff
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.PointsDiff
import com.etologic.mahjongscoring3.domain.model.TableDiffs
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.getNeededPointsByDirectHu
import com.etologic.mahjongscoring3.domain.model.getNeededPointsByIndirectHu
import com.etologic.mahjongscoring3.domain.model.getNeededPointsBySelfPick
import com.etologic.mahjongscoring3.domain.usecases.DeleteGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToTextUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring3.testutil.GameReport
import com.etologic.mahjongscoring3.testutil.Goldens
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Port of MS2 GameScenariosGoldenTest, NeededPointsTest goldens and export file names.
 *
 * MS2 builds every scenario in code and pins the result in golden files. MS3 does the reverse: it imports
 * each `scenarios/<name>/game.json` (MS2 export format) and must reproduce exactly the same report, CSV,
 * text and JSON. Each test checks every scenario and reports all the differences at once.
 */
class GameScenariosGoldenTest {

    private class Env {
        val games = FakeGamesRepository()
        val rounds = FakeRoundsRepository()
        val getOneGame = GetOneGameFlowUseCase(games, rounds)
        val getAllGames = GetAllGamesFlowUseCase(games, rounds)
        val importer = ImportGamesFromJsonUseCase(games, rounds, DeleteGameUseCase(games, rounds))

        suspend fun import(json: String): List<GameId> = importer(json).getOrThrow()
        suspend fun load(gameId: GameId): UiGame = getOneGame(gameId).first()
    }

    /** Scenario names, in MS2 order, from scenarios/INDEX.md ("- name: description"). */
    private val scenarios: List<String> =
        Goldens.read("scenarios/INDEX.md").lines()
            .filter { it.startsWith("- ") }
            .map { it.removePrefix("- ").substringBefore(":") }

    private suspend fun forEachScenario(check: suspend (name: String, env: Env, game: UiGame) -> Unit) {
        val failures = mutableListOf<String>()
        scenarios.forEach { name ->
            try {
                val env = Env()
                val id = env.import(Goldens.read("scenarios/$name/game.json")).single()
                check(name, env, env.load(id))
            } catch (e: Throwable) {
                failures += "[$name] ${e.message}"
            }
        }
        if (failures.isNotEmpty()) {
            fail("${failures.size} of ${scenarios.size} scenarios differ from MS2:\n\n" + failures.joinToString("\n\n"))
        }
    }

    @Test
    fun scenariosAreListed() {
        assertEquals(38, scenarios.size, "Scenarios in INDEX.md")
    }

    @Test
    fun importedGameReproducesMs2Report() = runTest {
        forEachScenario { name, _, game -> Goldens.assertMatches("scenarios/$name/report.txt", GameReport.render(game)) }
    }

    @Test
    fun jsonExportIsIdenticalToMs2() = runTest {
        forEachScenario { name, env, game ->
            val file = ExportGameToJsonUseCase(env.getOneGame)(game.gameId).getOrThrow()
            Goldens.assertMatches("scenarios/$name/game.json", file.content)
        }
    }

    @Test
    fun csvExportIsIdenticalToMs2() = runTest {
        forEachScenario { name, env, game ->
            val file = ExportGameToCsvUseCase(env.getOneGame)(game.gameId).getOrThrow()
            Goldens.assertMatches("scenarios/$name/export.csv", file.content)
        }
    }

    @Test
    fun textExportIsIdenticalToMs2() = runTest {
        forEachScenario { name, env, game ->
            val text = ExportGameToTextUseCase(env.getOneGame)(game.gameId).getOrThrow()
            Goldens.assertMatches("scenarios/$name/export.txt", text)
        }
    }

    @Test
    fun exportedFileNamesAreIdenticalToMs2() = runTest {
        val text = buildString {
            scenarios.forEach { name ->
                val env = Env()
                val id = env.import(Goldens.read("scenarios/$name/game.json")).single()
                val csv = ExportGameToCsvUseCase(env.getOneGame)(id).getOrThrow()
                val json = ExportGameToJsonUseCase(env.getOneGame)(id).getOrThrow()
                appendLine("$name: ${csv.fileName} | ${json.fileName}")
            }
            val env = Env()
            env.import(Goldens.read("all_games_export.json"))
            val all = ExportGamesToJsonUseCase(env.getAllGames)().getOrThrow()
            appendLine("all games: ${all.fileName}")
        }
        Goldens.assertMatches("export_file_names.txt", text)
    }

    @Test
    fun allGamesExportRoundTripsLikeMs2() = runTest {
        val goldenJson = Goldens.read("all_games_export.json")
        val env = Env()
        val ids = env.import(goldenJson)
        // MS2 lists (and exports) games newest first
        val export = ExportGamesToJsonUseCase(env.getAllGames)().getOrThrow()
        Goldens.assertMatches("all_games_export.json", export.content)

        val target = Env()
        val reimportedIds = target.import(export.content)
        assertEquals(ids.size, reimportedIds.size)
        val originalReports = env.getAllGames().first().map(GameReport::render)
        val reimportedReports = target.getAllGames().first().map(GameReport::render)
        assertEquals(originalReports, reimportedReports)
    }

    @Test
    fun neededPointsTableIsIdenticalToMs2() {
        val csv = buildString {
            appendLine("diff,selfPick,directHu,indirectHu")
            for (diff in 0..2000) {
                appendLine("$diff,${diff.getNeededPointsBySelfPick()},${diff.getNeededPointsByDirectHu()},${diff.getNeededPointsByIndirectHu()}")
            }
        }
        Goldens.assertMatches("needed_points.csv", csv)
        // The diffs calculator screen uses the same functions
        listOf(0, 40, 48, 64, 101).forEach { diff ->
            val row = Diff(diff)
            assertEquals(
                listOf(diff.getNeededPointsBySelfPick(), diff.getNeededPointsByDirectHu(), diff.getNeededPointsByIndirectHu()),
                listOf(row.selfPick, row.directHu, row.indirectHu),
            )
        }
    }

    @Test
    fun tableDiffsForEveryOrderingPatternAreIdenticalToMs2() {
        val values = listOf(-64, 0, 16, 64, 200)
        val text = buildString {
            for (e in values) for (s in values) for (w in values) for (n in values) {
                val diffs = TableDiffs(e, s, w, n).seatsDiffs
                assertEquals(TableWinds.asArray.toList(), diffs.map { it.seat })
                append("[$e,$s,$w,$n] ")
                appendLine(diffs.joinToString(" ") { d -> "${d.seat.name[0]}:${d.pointsToBeFirst.s()}/${d.pointsToBeSecond.s()}/${d.pointsToBeThird.s()}" })
            }
        }
        Goldens.assertMatches("table_diffs.txt", text)
    }

    private fun PointsDiff?.s() = this?.let { "${it.bySelfPick},${it.byDirectHu},${it.byIndirectHu}" } ?: "-"
}
