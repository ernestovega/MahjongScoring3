package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import screens.common.data.DefaultGamesDao
import screens.common.data.DefaultRoundsDao
import screens.common.data.GamesDao
import screens.common.data.RoundsDao
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
import screens.common.use_cases.ExportGamesToJsonUseCase
import screens.common.use_cases.ExportGameToCsvUseCase
import screens.common.use_cases.ExportGameToJsonUseCase
import screens.common.use_cases.ExportGameToTextUseCase
import screens.common.use_cases.GetAllGamesFlowUseCase
import screens.common.use_cases.GetOneGameFlowUseCase
import screens.common.use_cases.HuDiscardUseCase
import screens.common.use_cases.HuDrawUseCase
import screens.common.use_cases.HuSelfPickUseCase
import screens.common.use_cases.ImportGamesFromJsonUseCase
import screens.common.use_cases.ResumeGameUseCase
import screens.common.use_cases.SetPenaltyUseCase
import screens.help.diffs.DiffsScreenViewModel
import screens.help.diffs.repository.DefaultDiffsRepository
import screens.help.diffs.repository.DiffsRepository
import screens.help.fan.FanScreenViewModel
import screens.help.fan.repository.DefaultFanRepository
import screens.help.fan.repository.FanRepository
import screens.old_games.OldGamesScreenViewModel
import screens.game.GameScreenViewModel

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::FanScreenViewModel)
    viewModelOf(::DiffsScreenViewModel)
    viewModelOf(::OldGamesScreenViewModel)
    viewModelOf(::GameScreenViewModel)

    singleOf(::CancelAllPenaltiesUseCase)
    singleOf(::CreateGameUseCase)
    singleOf(::DeleteGameUseCase)
    singleOf(::DeleteRoundUseCase)
    singleOf(::EditGameNamesUseCase)
    singleOf(::EndGameUseCase)
    singleOf(::EndRoundUseCase)
    singleOf(::ExportGamesToJsonUseCase)
    singleOf(::ExportGameToCsvUseCase)
    singleOf(::ExportGameToJsonUseCase)
    singleOf(::ExportGameToTextUseCase)
    singleOf(::GetAllGamesFlowUseCase)
    singleOf(::GetOneGameFlowUseCase)
    singleOf(::HuDiscardUseCase)
    singleOf(::HuDrawUseCase)
    singleOf(::HuSelfPickUseCase)
    singleOf(::ImportGamesFromJsonUseCase)
    singleOf(::ResumeGameUseCase)
    singleOf(::SetPenaltyUseCase)

    singleOf(::DefaultDiffsRepository).bind<DiffsRepository>()
    singleOf(::DefaultFanRepository).bind<FanRepository>()
    singleOf(::DefaultRoundsRepository).bind<RoundsRepository>()
    singleOf(::DefaultGamesRepository).bind<GamesRepository>()
    
    singleOf(::DefaultRoundsDao).bind<RoundsDao>()
    singleOf(::DefaultGamesDao).bind<GamesDao>()
}