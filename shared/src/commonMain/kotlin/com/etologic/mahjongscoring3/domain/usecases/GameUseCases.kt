package com.etologic.mahjongscoring3.domain.usecases

import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.GameRoundsNumberExceededException
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.UiGame
import com.etologic.mahjongscoring3.domain.model.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring3.domain.model.toGameRecord
import com.etologic.mahjongscoring3.domain.model.toUiGame
import com.etologic.mahjongscoring3.domain.repositories.GamesRepository
import com.etologic.mahjongscoring3.domain.repositories.RoundsRepository
import com.etologic.mahjongscoring3.domain.utils.TimeProvider
import com.etologic.mahjongscoring3.domain.utils.normalizeName
import com.etologic.mahjongscoring3.domain.utils.toGameNameFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetAllGamesFlowUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(): Flow<List<UiGame>> =
        combine(
            gamesRepository.getAllFlow(),
            roundsRepository.getAllFlow(),
        ) { gameRecords, roundRecords ->
            val roundsByGame = roundRecords.groupBy { it.gameId }
            // Same as MS2 (fixed 2026-11-26): a game without rounds (for example, one just created whose first
            // round is not saved yet) is skipped instead of hiding the whole list.
            gameRecords.mapNotNull { gameRecord ->
                roundsByGame[gameRecord.gameId]
                    ?.sortedBy { it.roundId }
                    ?.let { gameRounds -> gameRecord.toUiGame(gameRounds) }
            }
        }
}

class GetOneGameFlowUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(gameId: GameId): Flow<UiGame> =
        combine(
            gamesRepository.getOneFlow(gameId),
            roundsRepository.getGameRoundsFlow(gameId),
        ) { gameRecord, roundRecords ->
            if (gameRecord == null || roundRecords.isEmpty()) {
                null
            } else {
                gameRecord.toUiGame(roundRecords.sortedBy { it.roundId })
            }
        }.filterNotNull()
}

class CreateGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val timeProvider: TimeProvider = TimeProvider.System,
) {
    suspend operator fun invoke(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ): Result<GameId> {
        val startDate = timeProvider.nowMillis()
        val newGame = GameRecord(
            gameId = NOT_SET_GAME_ID,
            // Stored as typed, as Mahjong Scoring 2 (names are only normalized when edited)
            gameName = gameName.ifBlank { startDate.toGameNameFormat() },
            nameP1 = nameP1,
            nameP2 = nameP2,
            nameP3 = nameP3,
            nameP4 = nameP4,
            startDate = startDate,
            endDate = null,
        )
        return gamesRepository.insertOne(newGame)
            .mapCatching { gameId ->
                roundsRepository.insertOne(RoundRecord(gameId = gameId)).getOrThrow()
                gameId
            }
    }
}

class DeleteGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        roundsRepository.deleteGameRounds(gameId)
            .mapCatching { gamesRepository.deleteOne(gameId).getOrThrow() }
}

class EditGameNamesUseCase(
    private val gamesRepository: GamesRepository,
) {
    suspend operator fun invoke(
        uiGame: UiGame,
        newGameName: String,
        newNameP1: String,
        newNameP2: String,
        newNameP3: String,
        newNameP4: String,
    ): Result<Boolean> =
        gamesRepository.updateOne(
            uiGame.toGameRecord().copy(
                nameP1 = normalizeName(newNameP1),
                nameP2 = normalizeName(newNameP2),
                nameP3 = normalizeName(newNameP3),
                nameP4 = normalizeName(newNameP4),
                gameName = normalizeName(newGameName).ifBlank { uiGame.startDate.toGameNameFormat() },
            )
        )
}

class EndGameUseCase(
    private val gamesRepository: GamesRepository,
    private val timeProvider: TimeProvider = TimeProvider.System,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        gamesRepository.updateOne(uiGame.toGameRecord(endDate = timeProvider.nowMillis()))
}

class ResumeGameUseCase(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        runCatching {
            if (uiGame.uiRounds.size < MAX_MCR_ROUNDS) {
                gamesRepository.updateOne(uiGame.toGameRecord(endDate = null)).getOrThrow()
                insertOngoingRoundIfNotPresent(uiGame)
            } else {
                throw GameRoundsNumberExceededException()
            }
        }

    private suspend fun insertOngoingRoundIfNotPresent(uiGame: UiGame): Boolean =
        if (uiGame.uiRounds.last().isOngoing().not()) {
            roundsRepository.insertOne(RoundRecord(gameId = uiGame.gameId)).getOrThrow()
        } else {
            false
        }
}

class EndRoundUseCase(
    private val roundsRepository: RoundsRepository,
    private val endGameUseCase: EndGameUseCase,
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<Boolean> =
        runCatching {
            val uiGame = getOneGameFlowUseCase(gameId).first()
            if (uiGame.uiRounds.size < MAX_MCR_ROUNDS) {
                roundsRepository.insertOne(RoundRecord(gameId = uiGame.gameId))
            } else {
                endGameUseCase(uiGame)
            }.getOrThrow()
        }
}
