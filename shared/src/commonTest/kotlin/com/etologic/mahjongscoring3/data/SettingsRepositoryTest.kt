package com.etologic.mahjongscoring3.data

import com.etologic.mahjongscoring3.data.settings.DataStoreSettingsRepository
import com.etologic.mahjongscoring3.data.settings.createDataStore
import com.etologic.mahjongscoring3.domain.model.SeatsArrangement
import com.etologic.mahjongscoring3.platform.SPANISH
import com.etologic.mahjongscoring3.testutil.PlatformTest
import com.etologic.mahjongscoring3.testutil.assertThat
import com.etologic.mahjongscoring3.testutil.testFilePath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Port of the DataStore part of MS2 LocalRepositoriesTest. MS2 has three repositories over the same
 * DataStore file; MS3 has one, with the same keys and defaults.
 */
class SettingsRepositoryTest : PlatformTest() {

    private fun repository(name: String) =
        DataStoreSettingsRepository(createDataStore { testFilePath("$name.preferences_pb") })

    @Test
    fun languageDefaultsToTheDeviceLanguageAndPersists() = runTest {
        val repository = repository("language")
        // MS2 returns the device language when nothing is saved; MS3 returns null, which means
        // "follow the device language" (same behaviour on screen, see App.kt).
        assertThat(repository.languageFlow.first()).isNull()
        repository.saveLanguage(SPANISH)
        assertThat(repository.languageFlow.first()).isEqualTo("es")
    }

    @Test
    fun seatsArrangementDefaultsToDownAndPersists() = runTest {
        val repository = repository("seats")
        assertThat(repository.seatsArrangementFlow.first()).isEqualTo(SeatsArrangement.DOWN)
        SeatsArrangement.entries.forEach {
            repository.saveSeatsArrangement(it)
            assertThat(repository.seatsArrangementFlow.first()).isEqualTo(it)
        }
    }

    @Test
    fun diffsCalculationFeatureDefaultsToEnabledAndPersists() = runTest {
        val repository = repository("diffs")
        assertThat(repository.isDiffsCalcsFeatureEnabledFlow.first()).isTrue()
        repository.saveDiffsCalcsFeatureEnabled(false)
        assertThat(repository.isDiffsCalcsFeatureEnabledFlow.first()).isFalse()
        repository.saveDiffsCalcsFeatureEnabled(true)
        assertThat(repository.isDiffsCalcsFeatureEnabledFlow.first()).isTrue()
    }
}
