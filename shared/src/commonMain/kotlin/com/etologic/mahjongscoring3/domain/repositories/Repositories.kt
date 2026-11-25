package com.etologic.mahjongscoring3.domain.repositories

import com.etologic.mahjongscoring3.domain.model.GameId
import com.etologic.mahjongscoring3.domain.model.GameRecord
import com.etologic.mahjongscoring3.domain.model.RoundId
import com.etologic.mahjongscoring3.domain.model.RoundRecord
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
    fun getAllFlow(): Flow<List<GameRecord>>
    fun getOneFlow(gameId: GameId): Flow<GameRecord?>
    suspend fun getOne(gameId: GameId): Result<GameRecord>
    suspend fun insertOne(gameRecord: GameRecord): Result<GameId>
    suspend fun updateOne(gameRecord: GameRecord): Result<Boolean>
    suspend fun deleteOne(gameId: GameId): Result<Boolean>
}

interface RoundsRepository {
    fun getAllFlow(): Flow<List<RoundRecord>>
    fun getGameRoundsFlow(gameId: GameId): Flow<List<RoundRecord>>
    suspend fun getGameRounds(gameId: GameId): Result<List<RoundRecord>>
    suspend fun getOne(roundId: RoundId): Result<RoundRecord>
    suspend fun insertOne(roundRecord: RoundRecord): Result<Boolean>
    suspend fun updateOne(roundRecord: RoundRecord): Result<Boolean>
    suspend fun deleteGameRounds(gameId: GameId): Result<Boolean>
    suspend fun deleteOne(roundId: RoundId): Result<Boolean>
}

interface SettingsRepository {
    val isDiffsCalcsFeatureEnabledFlow: Flow<Boolean>
    suspend fun saveDiffsCalcsFeatureEnabled(isEnabled: Boolean)

    val seatsArrangementFlow: Flow<SeatsArrangement>
    suspend fun saveSeatsArrangement(seatsArrangement: SeatsArrangement)

    /** Language code chosen in the app ("en", "es"...), or null to follow the system. */
    val languageFlow: Flow<String?>
    suspend fun saveLanguage(language: String)
}
