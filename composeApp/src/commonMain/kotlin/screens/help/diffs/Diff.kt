package screens.help.diffs

import androidx.compose.runtime.Immutable
import screens.common.ui.getNeededPointsByDirectHu
import screens.common.ui.getNeededPointsByIndirectHu
import screens.common.ui.getNeededPointsBySelfPick

@Immutable
data class Diff(
    val pointsNeeded: Int
) {
    val selfPick: Int = pointsNeeded.getNeededPointsBySelfPick()
    val directHu: Int = pointsNeeded.getNeededPointsByDirectHu()
    val indirectHu: Int = pointsNeeded.getNeededPointsByIndirectHu()
}