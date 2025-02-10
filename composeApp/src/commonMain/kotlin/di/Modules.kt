package di

import AppViewModel
import data.database.AppDatabase
import data.database.daos.GamesDao
import data.database.daos.RoundsDao
import data.repositories.diffs.DefaultDiffsRepository
import data.repositories.diffs.DiffsRepository
import data.repositories.fan.DefaultFanRepository
import data.repositories.fan.FanRepository
import data.repositories.games.DefaultGamesRepository
import data.repositories.games.GamesRepository
import data.repositories.rounds.DefaultRoundsRepository
import data.repositories.rounds.RoundsRepository
import domain.use_cases.CancelAllPenaltiesUseCase
import domain.use_cases.CreateGameUseCase
import domain.use_cases.DeleteGameUseCase
import domain.use_cases.DeleteRoundUseCase
import domain.use_cases.EditGameNamesUseCase
import domain.use_cases.EndGameUseCase
import domain.use_cases.EndRoundUseCase
import domain.use_cases.ExportGameToCsvUseCase
import domain.use_cases.ExportGameToJsonUseCase
import domain.use_cases.ExportGameToTextUseCase
import domain.use_cases.ExportGamesToJsonUseCase
import domain.use_cases.GetAllGamesFlowUseCase
import domain.use_cases.GetOneGameFlowUseCase
import domain.use_cases.HuDiscardUseCase
import domain.use_cases.HuDrawUseCase
import domain.use_cases.HuSelfPickUseCase
import domain.use_cases.ImportGamesFromJsonUseCase
import domain.use_cases.ResumeGameUseCase
import domain.use_cases.SetPenaltyUseCase
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ui.dialogs.create_game.CreateGameDialogViewModel
import ui.dialogs.hand_actions.HandActionsDialogViewModel
import ui.dialogs.penalty.PenaltyDialogViewModel
import ui.screens.game.GameScreenViewModel
import ui.screens.help.diffs.DiffsScreenViewModel
import ui.screens.help.fan.FanScreenViewModel
import ui.screens.old_games.OldGamesScreenViewModel

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
    viewModelOf(::PenaltyDialogViewModel)

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
