package data.repositories.diffs

import kotlinx.coroutines.flow.Flow
import domain.model.Diff

interface DiffsRepository{

    val getDiffsFlow: Flow<List<Diff>>

    suspend fun loadNextInterval()
}