package com.etologic.mahjongscoring3.ms2

import com.etologic.mahjongscoring3.domain.FakeGamesRepository
import com.etologic.mahjongscoring3.domain.FakeRoundsRepository
import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.HuData
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.PenaltyData
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.model.ShareGameOptions
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.usecases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring3.domain.usecases.CreateGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.DeleteGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.DeleteRoundUseCase
import com.etologic.mahjongscoring3.domain.usecases.EditGameNamesUseCase
import com.etologic.mahjongscoring3.domain.usecases.EndGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.EndRoundUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGameToTextUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.domain.usecases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDiscardUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDrawUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuSelfPickUseCase
import com.etologic.mahjongscoring3.domain.usecases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ResumeGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.SetPenaltyUseCase
import com.etologic.mahjongscoring3.testutil.FakeSettingsRepository
import com.etologic.mahjongscoring3.testutil.GameScenario
import com.etologic.mahjongscoring3.testutil.GameScenarios
import com.etologic.mahjongscoring3.testutil.Goldens
import com.etologic.mahjongscoring3.testutil.assertThat
import com.etologic.mahjongscoring3.ui.combinations.Combination
import com.etologic.mahjongscoring3.ui.combinations.ResolvedCombination
import com.etologic.mahjongscoring3.ui.combinations.filterCombinations
import com.etologic.mahjongscoring3.ui.common.GameNames
import com.etologic.mahjongscoring3.ui.diffs.MAX_ITEMS
import com.etologic.mahjongscoring3.ui.diffs.diffsCalculatorRow
import com.etologic.mahjongscoring3.ui.game.GamePageEvent
import com.etologic.mahjongscoring3.ui.game.GameUiState
import com.etologic.mahjongscoring3.ui.game.GameViewModel
import com.etologic.mahjongscoring3.ui.main.MainViewModel
import com.etologic.mahjongscoring3.ui.oldgames.OldGamesUiState
import com.etologic.mahjongscoring3.ui.oldgames.OldGamesViewModel
import com.etologic.mahjongscoring3.domain.model.Diff
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Port of MS2 GameViewModelTest and OtherViewModelsTest.
 *
 * Differences of the MS3 API, same behavior:
 * - MS2 GameUiState.Loaded carried selectedSeat, shouldShowDiffs, pageToShow and error; MS3 exposes them as separate flows
 *   (selectedSeat, shouldShowDiffs, pageEvent, error).
 * - MS2 pageToShow (LIST, HIGHLIGHT_LAST_ROUND) / (TABLE, SHOW_RANKING) is MS3 GamePageEvent.HighlightLastRound / ShowRanking.
 * - Exports are returned as ExportedFile (name + content) instead of files written to a folder.
 * - The diffs calculator and combinations screens have no ViewModel in MS3: their logic is tested through
 *   diffsCalculatorRow and filterCombinations. Combinations in the 6 languages are checked by the UI tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun test(block: suspend TestScope.() -> Unit) = runTest(dispatcher) { block() }

    private suspend fun <T> Flow<T>.awaitFirst(predicate: (T) -> Boolean): T = withTimeout(5_000) { first(predicate) }
    private suspend fun <T> CompletableDeferred<T>.awaitValue(): T = withTimeout(5_000) { await() }

    private suspend fun seed(games: FakeGamesRepository, rounds: FakeRoundsRepository, scenario: GameScenario) {
        games.insertOne(scenario.game).getOrThrow()
        scenario.rounds.forEach { rounds.insertOne(it).getOrThrow() }
    }

    // region GameViewModel

    private inner class GameEnv {
        val games = FakeGamesRepository()
        val rounds = FakeRoundsRepository()
        val settings = FakeSettingsRepository()
        private val getOneGame = GetOneGameFlowUseCase(games, rounds)
        private val endGame = EndGameUseCase(games)
        private val endRound = EndRoundUseCase(rounds, endGame, getOneGame)
        val viewModel = GameViewModel(
            getOneGameFlowUseCase = getOneGame,
            editGameNamesUseCase = EditGameNamesUseCase(games),
            huDiscardUseCase = HuDiscardUseCase(rounds, endRound),
            huSelfPickUseCase = HuSelfPickUseCase(rounds, endRound),
            huDrawUseCase = HuDrawUseCase(rounds, endRound),
            setPenaltyUseCase = SetPenaltyUseCase(rounds),
            cancelAllPenaltiesUseCase = CancelAllPenaltiesUseCase(rounds),
            deleteRoundUseCase = DeleteRoundUseCase(rounds),
            resumeGameUseCase = ResumeGameUseCase(games, rounds),
            endGameUseCase = endGame,
            exportGameToTextUseCase = ExportGameToTextUseCase(getOneGame),
            exportGameToCsvUseCase = ExportGameToCsvUseCase(getOneGame),
            exportGameToJsonUseCase = ExportGameToJsonUseCase(getOneGame),
            settingsRepository = settings,
        )

        suspend fun open(scenario: GameScenario): GameUiState.Loaded {
            seed(games, rounds, scenario)
            viewModel.setGameId(scenario.game.gameId)
            return awaitLoaded { it.game.gameId == scenario.game.gameId }
        }

        suspend fun awaitLoaded(predicate: (GameUiState.Loaded) -> Boolean): GameUiState.Loaded =
            viewModel.uiState.awaitFirst { it is GameUiState.Loaded && predicate(it) } as GameUiState.Loaded
    }

    @Test
    fun initialStateIsLoadingUntilAGameIdIsSet() = test {
        assertThat(GameEnv().viewModel.uiState.value).isEqualTo(GameUiState.Loading)
    }

    @Test
    fun loadedStateDefaults() = test {
        val env = GameEnv()
        val state = env.open(GameScenarios.penalties)
        assertThat(env.viewModel.error.value).isNull()
        assertThat(state.isDiffsCalcsFeatureEnabled).isTrue()
        assertThat(env.viewModel.shouldShowDiffs.value).isFalse()
        assertThat(state.seatsArrangement).isEqualTo(SeatsArrangement.DOWN)
        assertThat(env.viewModel.pageEvent.value).isNull()
        assertThat(env.viewModel.selectedSeat.value).isEqualTo(TableWinds.NONE)
        assertThat(state.game.uiRounds).hasSize(5)
    }

    @Test
    fun seatSelection() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.onSeatClicked(TableWinds.SOUTH)
        env.viewModel.selectedSeat.awaitFirst { it == TableWinds.SOUTH }
        env.viewModel.unselectSeats()
        env.viewModel.selectedSeat.awaitFirst { it == TableWinds.NONE }
    }

    @Test
    fun savingAHandShowsTheListHighlightingTheLastRound() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.saveHuDiscardRound(HuData(points = 8, winnerInitialSeat = TableWinds.EAST, discarderInitialSeat = TableWinds.WEST))
        val state = env.awaitLoaded { it.game.uiRounds.size == 2 }
        env.viewModel.pageEvent.awaitFirst { it is GamePageEvent.HighlightLastRound }
        assertThat(state.game.uiRounds[0].totalPointsP1).isEqualTo(32)
        assertThat(state.game.uiRounds[0].totalPointsP3).isEqualTo(-16)
    }

    @Test
    fun selfPickAndDraw() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.saveHuSelfPickRound(HuData(points = 10, winnerInitialSeat = TableWinds.NORTH))
        env.awaitLoaded { it.game.uiRounds.size == 2 }
        env.viewModel.saveDrawRound()
        val state = env.awaitLoaded { it.game.uiRounds.size == 3 }
        assertThat(state.game.uiRounds.map { it.winnerInitialSeat }).containsExactly(TableWinds.NORTH, TableWinds.NONE, null).inOrder()
        assertThat(state.game.ongoingRound.totalPointsP4).isEqualTo(54)
    }

    @Test
    fun penaltiesAreSetOnTheOngoingRoundAndCanBeCancelled() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.savePenalty(PenaltyData(points = 30, isDivided = true, penalizedPlayerInitialSeat = TableWinds.WEST))
        val state = env.awaitLoaded { it.game.ongoingRound.areTherePenalties() }
        with(state.game.ongoingRound) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(10, 10, -30, 10).inOrder()
        }
        env.viewModel.cancelPenalties()
        env.awaitLoaded { !it.game.ongoingRound.areTherePenalties() }
    }

    @Test
    fun removeRound() = test {
        val env = GameEnv()
        val state = env.open(GameScenarios.selfPicks)
        env.viewModel.removeRound(state.game.uiRounds[0].roundId)
        val updated = env.awaitLoaded { it.game.uiRounds.size == 4 }
        assertThat(updated.game.uiRounds[0].winnerInitialSeat).isEqualTo(TableWinds.SOUTH)
    }

    @Test
    fun editNames() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.saveGameNames(GameNames("Renamed", "W", "X", "Y", "Z"))
        val updated = env.awaitLoaded { it.game.gameName == "Renamed" }
        assertThat(updated.game.playersNames.toList()).containsExactly("W", "X", "Y", "Z").inOrder()
    }

    @Test
    fun endingAGameAsksForTheInAppReview() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        val ended = CompletableDeferred<Unit>()
        env.viewModel.endGame { ended.complete(Unit) }
        ended.awaitValue()
        env.awaitLoaded { it.game.isEnded }
    }

    @Test
    fun the16thHandEndsTheGameAndShowsTheRanking() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        repeat(15) { index ->
            env.viewModel.saveDrawRound()
            env.awaitLoaded { it.game.uiRounds.size == index + 2 }
        }
        env.viewModel.saveHuSelfPickRound(HuData(points = 8, winnerInitialSeat = TableWinds.EAST))
        env.viewModel.pageEvent.awaitFirst { it is GamePageEvent.ShowRanking }
        // Showing the ranking switches the persisted seats arrangement to RANKING.
        val state = env.awaitLoaded { it.game.isEnded && it.seatsArrangement == SeatsArrangement.RANKING }
        assertThat(state.game.uiRounds).hasSize(16)
        assertThat(env.settings.seatsArrangement.value).isEqualTo(SeatsArrangement.RANKING)
    }

    @Test
    fun resumeAnEndedGame() = test {
        val env = GameEnv()
        env.open(GameScenarios.allDiscardPairs)
        env.viewModel.resumeGame()
        val state = env.awaitLoaded { !it.game.isEnded }
        assertThat(state.game.uiRounds).hasSize(13)
        assertThat(state.game.ongoingRound.isOngoing()).isTrue()
    }

    @Test
    fun seatsArrangementCyclesDownOutRanking() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.nextSeatsOrientation()
        env.awaitLoaded { it.seatsArrangement == SeatsArrangement.OUT }
        env.viewModel.nextSeatsOrientation()
        env.awaitLoaded { it.seatsArrangement == SeatsArrangement.RANKING }
        env.viewModel.nextSeatsOrientation()
        env.awaitLoaded { it.seatsArrangement == SeatsArrangement.DOWN }
    }

    @Test
    fun diffsCanOnlyBeShownWhileTheFeatureIsEnabled() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.viewModel.toggleDiffsView(true)
        env.viewModel.shouldShowDiffs.awaitFirst { it }
        env.viewModel.toggleDiffsView(false)
        env.viewModel.shouldShowDiffs.awaitFirst { !it }
        env.viewModel.setDiffsCalcsFeatureEnabled(false)
        env.awaitLoaded { !it.isDiffsCalcsFeatureEnabled }
        env.viewModel.toggleDiffsView(true)
        assertThat(env.viewModel.shouldShowDiffs.value).isFalse()
        env.viewModel.setDiffsCalcsFeatureEnabled(true)
        env.awaitLoaded { it.isDiffsCalcsFeatureEnabled }
    }

    @Test
    fun errorsAreExposed() = test {
        val env = GameEnv()
        env.open(GameScenarios.newGame)
        env.rounds.failure = IllegalStateException("disk full")
        env.viewModel.savePenalty(PenaltyData(10, false, TableWinds.EAST))
        val error = env.viewModel.error.awaitFirst { it != null }
        assertThat(error?.message).isEqualTo("disk full")
    }

    @Test
    fun shareAsTextCsvAndJsonProduceTheGoldenExports() = test {
        val env = GameEnv()
        env.open(GameScenarios.fullGame)
        val dir = "scenarios/${GameScenarios.fullGame.name}"

        val text = CompletableDeferred<String>()
        env.viewModel.shareGame(ShareGameOptions.TEXT, { text.complete(it) }, { })
        assertThat(text.awaitValue()).isEqualTo(Goldens.read("$dir/export.txt"))

        val csv = CompletableDeferred<ExportedFile>()
        env.viewModel.shareGame(ShareGameOptions.CSV, { }, { csv.complete(it) })
        assertThat(csv.awaitValue().content).isEqualTo(Goldens.read("$dir/export.csv"))

        val json = CompletableDeferred<ExportedFile>()
        env.viewModel.shareGame(ShareGameOptions.JSON, { }, { json.complete(it) })
        assertThat(json.awaitValue().content).isEqualTo(Goldens.read("$dir/game.json"))
    }

    // endregion

    // region MainViewModel

    @Test
    fun mainViewModelKeepsTheActiveGameId() = test {
        val viewModel = MainViewModel(FakeSettingsRepository())
        assertThat(viewModel.activeGameId.value).isEqualTo(NOT_SET_GAME_ID)
        viewModel.updateActiveGameId(7)
        assertThat(viewModel.activeGameId.value).isEqualTo(7L)
    }

    // endregion

    // region Diffs calculator (no ViewModel in MS3)

    @Test
    fun diffsCalculatorStartsAt32AndHas2000Rows() {
        val rows = (0 until MAX_ITEMS).map(::diffsCalculatorRow)
        assertThat(rows.first()).isEqualTo(Diff(32))
        assertThat(rows[199]).isEqualTo(Diff(231))
        assertThat(rows.map { it.pointsNeeded }).isEqualTo((32 until 2032).toList())
        assertThat(MAX_ITEMS).isEqualTo(2000)
    }

    // endregion

    // region Combinations filter (no ViewModel in MS3)

    private fun resolved(vararg items: Triple<Int, String, String?>) = items.mapIndexed { index, (points, name, description) ->
        ResolvedCombination(
            Combination(index + 1, points, com.etologic.mahjongscoring3.resources.Res.string.dragon_pung),
            name,
            description,
        )
    }

    @Test
    fun combinationsCanBeFilteredByNameDescriptionOrPointsIgnoringCase() {
        val all = resolved(
            Triple(2, "Dragon pung", null),
            Triple(6, "Two dragon pungs", null),
            Triple(88, "Big four winds", null),
            Triple(1, "Edge wait", "Waiting for the 3 of a 1-2"),
            Triple(8, "Melded kong", null),
        )
        assertThat(filterCombinations(all, "").map { it.name }).hasSize(5)
        assertThat(filterCombinations(all, "dragon").map { it.name }).containsExactly("Dragon pung", "Two dragon pungs").inOrder()
        assertThat(filterCombinations(all, "88").map { it.name }).containsExactly("Big four winds")
        assertThat(filterCombinations(all, "WAITING").map { it.name }).containsExactly("Edge wait")
        assertThat(filterCombinations(all, "KONG").map { it.name }).containsExactly("Melded kong")
        assertThat(filterCombinations(all, "zzzz")).isEmpty()
    }

    // endregion

    // region OldGamesViewModel

    private inner class OldGamesEnv {
        val games = FakeGamesRepository()
        val rounds = FakeRoundsRepository()
        private val getOne = GetOneGameFlowUseCase(games, rounds)
        private val getAll = GetAllGamesFlowUseCase(games, rounds)
        val viewModel = OldGamesViewModel(
            getAllGamesFlowUseCase = getAll,
            createGameUseCase = CreateGameUseCase(games, rounds),
            deleteGameUseCase = DeleteGameUseCase(games, rounds),
            exportGameToTextUseCase = ExportGameToTextUseCase(getOne),
            exportGameToCsvUseCase = ExportGameToCsvUseCase(getOne),
            exportGameToJsonUseCase = ExportGameToJsonUseCase(getOne),
            exportGamesToJsonUseCase = ExportGamesToJsonUseCase(getAll),
            importGamesFromJsonUseCase = ImportGamesFromJsonUseCase(games, rounds, DeleteGameUseCase(games, rounds)),
        )

        suspend fun seedHandWritten() = GameScenarios.handWritten.forEach { seed(games, rounds, it) }

        suspend fun awaitGames(predicate: (List<UiGame>) -> Boolean): List<UiGame> =
            (viewModel.uiState.awaitFirst { it is OldGamesUiState.Loaded && predicate(it.oldGamesList) } as OldGamesUiState.Loaded).oldGamesList
    }

    @Test
    fun oldGamesAreListedNewestFirst() = test {
        val env = OldGamesEnv().apply { seedHandWritten() }
        assertThat(env.viewModel.uiState.value).isEqualTo(OldGamesUiState.Loading)
        val list = env.awaitGames { it.isNotEmpty() }
        assertThat(list.map { it.gameId }).isEqualTo(GameScenarios.handWritten.sortedByDescending { it.game.startDate }.map { it.game.gameId })
    }

    @Test
    fun createAndDeleteGames() = test {
        val env = OldGamesEnv()
        assertThat(env.awaitGames { true }).isEmpty()
        val created = CompletableDeferred<GameId>()
        env.viewModel.createGame(GameNames("New", "A", "B", "C", "D")) { created.complete(it) }
        val id = created.awaitValue()
        assertThat(env.awaitGames { it.size == 1 }.single().gameId).isEqualTo(id)
        env.viewModel.deleteGame(id)
        assertThat(env.awaitGames { it.isEmpty() }).isEmpty()
    }

    @Test
    fun oldGamesErrorsAreExposed() = test {
        val env = OldGamesEnv()
        env.awaitGames { true }
        env.games.failure = IllegalStateException("boom")
        env.viewModel.createGame(GameNames("New", "A", "B", "C", "D")) { }
        val error = env.viewModel.error.awaitFirst { it != null }
        assertThat(error?.message).isEqualTo("boom")
    }

    @Test
    fun shareAGameInTheThreeFormats() = test {
        val env = OldGamesEnv().apply { seedHandWritten() }
        val scenario = GameScenarios.specialNames
        val dir = "scenarios/${scenario.name}"

        val text = CompletableDeferred<String>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.TEXT, { text.complete(it) }, {})
        assertThat(text.awaitValue()).isEqualTo(Goldens.read("$dir/export.txt"))

        val csv = CompletableDeferred<ExportedFile>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.CSV, {}, { csv.complete(it) })
        assertThat(csv.awaitValue().content).isEqualTo(Goldens.read("$dir/export.csv"))

        val json = CompletableDeferred<ExportedFile>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.JSON, {}, { json.complete(it) })
        assertThat(json.awaitValue().content).isEqualTo(Goldens.read("$dir/game.json"))
    }

    @Test
    fun exportAllGamesAndImportThemBack() = test {
        val source = OldGamesEnv().apply { seedHandWritten() }
        source.awaitGames { it.isNotEmpty() }
        val exported = CompletableDeferred<ExportedFile>()
        source.viewModel.exportGames { exported.complete(it) }
        val file = exported.awaitValue()
        assertThat(file.fileName).isEqualTo("MahjongMadrid2_DataBase.json")
        assertThat(file.content).isEqualTo(Goldens.read("all_games_export.json"))

        val target = OldGamesEnv()
        target.viewModel.importGames(file.content)
        val imported = target.awaitGames { it.size == GameScenarios.handWritten.size }
        assertThat(imported.map { it.gameName }).isEqualTo(source.awaitGames { true }.map { it.gameName })
    }

    // endregion
}
