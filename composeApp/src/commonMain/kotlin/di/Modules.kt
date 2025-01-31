package di

import AppViewModel
import database.AppDatabase
import database.daos.GamesDao
import database.daos.RoundsDao
import dialogs.create_game.CreateGameDialogViewModel
import dialogs.hand_actions.HandActionsDialogViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import screens.common.data.games.DefaultGamesRepository
import screens.common.data.games.GamesRepository
import screens.common.data.rounds.DefaultRoundsRepository
import screens.common.data.rounds.RoundsRepository
import screens.common.use_cases.CancelAllPenaltiesUseCase
import screens.common.use_cases.CreateGameUseCase
import screens.common.use_cases.DeleteGameUseCase
import screens.common.use_cases.DeleteRoundUseCase
import screens.common.use_cases.EditGameNamesUseCase
import screens.common.use_cases.EndGameUseCase
import screens.common.use_cases.EndRoundUseCase
import screens.common.use_cases.ExportGameToCsvUseCase
import screens.common.use_cases.ExportGameToJsonUseCase
import screens.common.use_cases.ExportGameToTextUseCase
import screens.common.use_cases.ExportGamesToJsonUseCase
import screens.common.use_cases.GetAllGamesFlowUseCase
import screens.common.use_cases.GetOneGameFlowUseCase
import screens.common.use_cases.HuDiscardUseCase
import screens.common.use_cases.HuDrawUseCase
import screens.common.use_cases.HuSelfPickUseCase
import screens.common.use_cases.ImportGamesFromJsonUseCase
import screens.common.use_cases.ResumeGameUseCase
import screens.common.use_cases.SetPenaltyUseCase
import screens.game.GameScreenViewModel
import screens.help.diffs.DiffsScreenViewModel
import screens.help.diffs.repository.DefaultDiffsRepository
import screens.help.diffs.repository.DiffsRepository
import screens.help.fan.FanScreenViewModel
import screens.help.fan.repository.DefaultFanRepository
import screens.help.fan.repository.FanRepository
import screens.old_games.OldGamesScreenViewModel

expect val platformModule: Module

val sharedModule = module {
    //Screens ViewModels
    viewModelOf(::AppViewModel)
    viewModelOf(::FanScreenViewModel)
    viewModelOf(::DiffsScreenViewModel)
    viewModelOf(::OldGamesScreenViewModel)
    viewModelOf(::GameScreenViewModel)

    //Dialogs ViewModels
    viewModelOf(::CreateGameDialogViewModel)
    viewModelOf(::HandActionsDialogViewModel)

    //UseCases
    factoryOf(::CancelAllPenaltiesUseCase)
    factoryOf(::CreateGameUseCase)
    factoryOf(::DeleteGameUseCase)
    factoryOf(::DeleteRoundUseCase)
    factoryOf(::EditGameNamesUseCase)
    factoryOf(::EndGameUseCase)
    factoryOf(::EndRoundUseCase)
    factoryOf(::ExportGamesToJsonUseCase)
    factoryOf(::ExportGameToCsvUseCase)
    factoryOf(::ExportGameToJsonUseCase)
    factoryOf(::ExportGameToTextUseCase)
    factoryOf(::GetAllGamesFlowUseCase)
    factoryOf(::GetOneGameFlowUseCase)
    factoryOf(::HuDiscardUseCase)
    factoryOf(::HuDrawUseCase)
    factoryOf(::HuSelfPickUseCase)
    factoryOf(::ImportGamesFromJsonUseCase)
    factoryOf(::ResumeGameUseCase)
    factoryOf(::SetPenaltyUseCase)

    //Repositories
    singleOf(::DefaultDiffsRepository).bind<DiffsRepository>()
    singleOf(::DefaultFanRepository).bind<FanRepository>()
    singleOf(::DefaultRoundsRepository).bind<RoundsRepository>()
    singleOf(::DefaultGamesRepository).bind<GamesRepository>()
//    singleOf(::FakeRoundsRepository).bind<RoundsRepository>()
//    singleOf(::FakeGamesRepository).bind<GamesRepository>()

    //Database
    single<GamesDao> { get<AppDatabase>().gamesDao }
    single<RoundsDao> { get<AppDatabase>().roundsDao }
}
