package data.repositories.diffs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ui.screens.help.diffs.Diff


class DefaultDiffsRepository: DiffsRepository {

    private val _diffs: MutableStateFlow<List<Diff>> = MutableStateFlow(getFirstInterval())

    override val getDiffsFlow: Flow<List<Diff>> = _diffs

    private fun getFirstInterval(): List<Diff> =
        mutableListOf<Diff>().apply {
            for (i in MIN_DIFF..<MIN_DIFF + NUM_CALCS_INTERVAL) {
                add(Diff(i))
            }
        }

    override suspend fun loadNextInterval() {
        if (_diffs.value.size < MAX_ITEMS) {
            val diffListCopy = _diffs.value.toMutableList()
            val nextInterval = getNextInterval()
            diffListCopy.addAll(nextInterval)
            _diffs.emit(diffListCopy)
        }
    }

    private fun getNextInterval(): List<Diff> =
        mutableListOf<Diff>().apply {
            val numCurrentIntervals = _diffs.value.size / NUM_CALCS_INTERVAL
            if (numCurrentIntervals < MAX_INTERVALS) {
                val nextFirstPointsNeeded = NUM_CALCS_INTERVAL * numCurrentIntervals + MIN_DIFF
                for (i in nextFirstPointsNeeded..<nextFirstPointsNeeded + NUM_CALCS_INTERVAL) {
                    add(Diff(i))
                }
            }
        }

    companion object {
        private const val MIN_DIFF: Int = 32
        private const val NUM_CALCS_INTERVAL = 200
        private const val MAX_INTERVALS = 10
        private const val MAX_ITEMS = MAX_INTERVALS * NUM_CALCS_INTERVAL
    }
}