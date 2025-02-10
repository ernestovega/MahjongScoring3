package data.repositories.diffs

import kotlinx.coroutines.flow.Flow
import ui.screens.help.diffs.Diff

interface DiffsRepository{

    val getDiffsFlow: Flow<List<Diff>>

    suspend fun loadNextInterval()
}