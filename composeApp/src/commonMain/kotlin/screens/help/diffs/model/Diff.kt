package screens.help.diffs.model

import screens.common.getNeededPointsByDirectHu
import screens.common.getNeededPointsByIndirectHu
import screens.common.getNeededPointsBySelfPick

data class Diff(
    val pointsNeeded: Int
) {
    val selfPick: Int = pointsNeeded.getNeededPointsBySelfPick()
    val directHu: Int = pointsNeeded.getNeededPointsByDirectHu()
    val indirectHu: Int = pointsNeeded.getNeededPointsByIndirectHu()
}