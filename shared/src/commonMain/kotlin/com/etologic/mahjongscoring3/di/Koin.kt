package com.etologic.mahjongscoring3.di

import com.etologic.mahjongscoring3.data.database.AppDatabase
import com.etologic.mahjongscoring3.data.repositories.RoomGamesRepository
import com.etologic.mahjongscoring3.data.repositories.RoomRoundsRepository
import com.etologic.mahjongscoring3.data.settings.DataStoreSettingsRepository
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
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
import com.etologic.mahjongscoring3.domain.usecases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDiscardUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuDrawUseCase
import com.etologic.mahjongscoring3.domain.usecases.HuSelfPickUseCase
import com.etologic.mahjongscoring3.domain.usecases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring3.domain.usecases.ResumeGameUseCase
import com.etologic.mahjongscoring3.domain.usecases.SetPenaltyUseCase
import com.etologic.mahjongscoring3.ui.game.GameViewModel
import com.etologic.mahjongscoring3.ui.main.MainViewModel
import com.etologic.mahjongscoring3.ui.oldgames.OldGamesViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Provides AppDatabase and DataStore<Preferences>, built with the platform paths. */
expect val platformModule: Module

val dataModule = module {
    single { get<AppDatabase>().gamesDao() }
    single { get<AppDatabase>().roundsDao() }
    singleOf(::RoomGamesRepository) bind GamesRepository::class
    singleOf(::RoomRoundsRepository) bind RoundsRepository::class
    singleOf(::DataStoreSettingsRepository) bind SettingsRepository::class
}

val domainModule = module {
    factoryOf(::GetAllGamesFlowUseCase)
    factoryOf(::GetOneGameFlowUseCase)
    factory { CreateGameUseCase(get(), get()) }
    factoryOf(::DeleteGameUseCase)
    factoryOf(::EditGameNamesUseCase)
    factory { EndGameUseCase(get()) }
    factoryOf(::ResumeGameUseCase)
    factoryOf(::EndRoundUseCase)
    factoryOf(::HuDiscardUseCase)
    factoryOf(::HuSelfPickUseCase)
    factoryOf(::HuDrawUseCase)
    factoryOf(::DeleteRoundUseCase)
    factoryOf(::SetPenaltyUseCase)
    factoryOf(::CancelAllPenaltiesUseCase)
    factoryOf(::ExportGameToTextUseCase)
    factoryOf(::ExportGameToCsvUseCase)
    factoryOf(::ExportGameToJsonUseCase)
    factoryOf(::ExportGamesToJsonUseCase)
    factoryOf(::ImportGamesFromJsonUseCase)
}

val uiModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::OldGamesViewModel)
    viewModelOf(::GameViewModel)
}

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    startKoin {
        appDeclaration()
        modules(platformModule, dataModule, domainModule, uiModule)
    }
}
