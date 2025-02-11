package domain.model

import androidx.compose.runtime.Immutable
import ui.common.components.getNeededPointsByDirectHu
import ui.common.components.getNeededPointsByIndirectHu
import ui.common.components.getNeededPointsBySelfPick

@Immutable
data class Diff(
    val pointsNeeded: Int
) {
    val selfPick: Int = pointsNeeded.getNeededPointsBySelfPick()
    val directHu: Int = pointsNeeded.getNeededPointsByDirectHu()
    val indirectHu: Int = pointsNeeded.getNeededPointsByIndirectHu()
}