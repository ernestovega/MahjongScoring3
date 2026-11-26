package com.etologic.mahjongscoring3.testutil

import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory settings with the same defaults as the MS2 DataStore repositories. */
class FakeSettingsRepository : SettingsRepository {
    val isDiffsCalcsFeatureEnabled = MutableStateFlow(true)
    val seatsArrangement = MutableStateFlow(SeatsArrangement.DOWN)
    val language = MutableStateFlow<String?>(null)

    override val isDiffsCalcsFeatureEnabledFlow = isDiffsCalcsFeatureEnabled
    override suspend fun saveDiffsCalcsFeatureEnabled(isEnabled: Boolean) { isDiffsCalcsFeatureEnabled.value = isEnabled }

    override val seatsArrangementFlow = seatsArrangement
    override suspend fun saveSeatsArrangement(seatsArrangement: SeatsArrangement) { this.seatsArrangement.value = seatsArrangement }

    override val languageFlow = language
    override suspend fun saveLanguage(language: String) { this.language.value = language }
}
