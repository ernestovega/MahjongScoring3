package screens.help.diffs.repository

import kotlinx.coroutines.flow.Flow
import screens.help.diffs.model.Diff

interface DiffsRepository{

    val getDiffsFlow: Flow<List<Diff>>

    suspend fun loadNextInterval()
}