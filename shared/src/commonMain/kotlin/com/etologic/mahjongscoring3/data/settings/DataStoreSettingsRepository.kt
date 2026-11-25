package com.etologic.mahjongscoring3.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

/** Same DataStore file as Mahjong Scoring 2: `preferencesDataStore(name = "MahjongScoring2")`. */
const val DATASTORE_FILE_NAME = "MahjongScoring2.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private companion object {
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_IS_DIFFS_CALCS_FEATURE_ENABLED = booleanPreferencesKey("isDiffsCalcsFeatureEnabled")
        val KEY_SELECTED_SEATS_ORIENTATION = intPreferencesKey("selectedSeatsOrientation")
    }

    override val isDiffsCalcsFeatureEnabledFlow: Flow<Boolean> =
        dataStore.data.map { it[KEY_IS_DIFFS_CALCS_FEATURE_ENABLED] ?: true }

    override suspend fun saveDiffsCalcsFeatureEnabled(isEnabled: Boolean) {
        dataStore.edit { it[KEY_IS_DIFFS_CALCS_FEATURE_ENABLED] = isEnabled }
    }

    override val seatsArrangementFlow: Flow<SeatsArrangement> =
        dataStore.data.map { SeatsArrangement.from(it[KEY_SELECTED_SEATS_ORIENTATION]) }

    override suspend fun saveSeatsArrangement(seatsArrangement: SeatsArrangement) {
        dataStore.edit { it[KEY_SELECTED_SEATS_ORIENTATION] = seatsArrangement.code }
    }

    override val languageFlow: Flow<String?> =
        dataStore.data.map { it[KEY_LANGUAGE] }

    override suspend fun saveLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }
}
